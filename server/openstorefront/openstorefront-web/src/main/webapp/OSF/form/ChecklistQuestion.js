/* 
 * Copyright 2016 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* global Ext, CoreUtil */

Ext.define('OSF.form.ChecklistQuestion', {
	extend: 'Ext.form.Panel',
	alias: 'osf.form.ChecklistQuestion',

	scrollable: true,
	dockedItems: [
		{
			xtype: 'toolbar',
			itemId: 'tools',
			dock: 'bottom',
			items: [		
				{
					xtype: 'combo',
					itemId: 'workflowStatus',
					name: 'workflowStatus',										
					labelAlign: 'right',												
					margin: '0 0 5 0',
					editable: false,
					typeAhead: false,
					width: 400,
					fieldLabel: 'Status <span class="field-required" />',	
					displayField: 'description',
					valueField: 'code',
					labelSeparator: '',
					store: {
						autoLoad: true,
						proxy: {
							type: 'ajax',
							url: 'api/v1/resource/lookuptypes/WorkflowStatus'
						}
					}			
				},
				{
					xtype: 'tbfill'
				},
				{
					xtype: 'tbtext',
					itemId: 'status'
				}				
			]
		}
	],
	initComponent: function () {		
		this.callParent();
		
		var questionForm = this;
		
		questionForm.response = Ext.create('Ext.panel.Panel',{
			bodyStyle: 'padding: 20px;',
			layout: 'anchor',
			
			defaults: {
				width: '100%',
				labelAlign: 'right'
			},
			dockedItems: [
				{
					xtype: 'panel',
					dock: 'top',
					itemId: 'question',
					title: 'Question',					
					animCollapse: false,
					collapsible: true,
					titleCollapse: true,
					bodyStyle: 'background: white; padding: 20px;',									
					tpl: new Ext.XTemplate(						
						'<div class="checklist-question">{question}</div>',
						'({evaluationSectionDescription})<br>',
						'<tpl if="scoringCriteria"><h3>Scoring criteria:</h3>',
						'{scoringCriteria}</tpl>',
						'<tpl if="objective"><h3>Objective:</h3>',
						'{objective}</tpl>',
						'<tpl if="narrative"><h3>Narrative:</h3>',
						'{narrative}</tpl>'
					)
				}
			],
			items: [
				{
					xtype: 'combobox',
					itemId: 'score',
					name: 'score',
					fieldCls: 'eval-form-field',
					labelClsExtra: 'eval-form-field-label',					
					fieldLabel: 'Score',
					margin: '0 0 0 -25',					
					width: 175,
					valueField: 'code',
					displayField: 'description',
					editable: false,
					typeAhead: false,
					store: {
						data: [
							{ code: '1', description: '1' },
							{ code: '2', description: '2' },
							{ code: '3', description: '3' },
							{ code: '4', description: '4' },
							{ code: '5', description: '5' }
						]
					}				
				},
				{
					xtype: 'checkbox',
					itemId: 'notApplicable',
					name: 'notApplicable',
					boxLabel: 'Not Applicable'
				},
				{
					xtype: 'panel',						
					html: '<b>Response</b>'
				},
				{
					xtype: 'tinymce_textarea',	
					itemId: 'response',
					fieldStyle: 'font-family: Courier New; font-size: 12px;',
					style: { border: '0' },
					height: 250,
					width: '100%',
					name: 'response',			
					maxLength: 32000,
					tinyMCEConfig: CoreUtil.tinymceConfig("osfmediaretriever")						
				},
				{
					xtype: 'panel',						
					html: '<b>Private Notes</b>'
				},
				{
					xtype: 'tinymce_textarea',
					itemId: 'privateNote',
					fieldStyle: 'font-family: Courier New; font-size: 12px;',
					style: { border: '0' },
					height: 250,
					width: '100%',
					name: 'privateNote',			
					maxLength: 32000,
					tinyMCEConfig: CoreUtil.tinymceConfig("osfmediaretriever")					
				}				
			]
		});
	
		questionForm.add(questionForm.response);
	},
	loadData: function(evaluationId, componentId, data, opts) {
		
		var questionForm = this;
		
		questionForm.response.getComponent('question').setTitle("Question - " + data.question.qid);
		
		questionForm.setLoading(true);
		Ext.Ajax.request({
			url: 'api/v1/resource/evaluations/' + evaluationId + '/checklist/' + data.checklistId + '/responses/' + data.responseId,
			callback: function() {
				questionForm.setLoading(false);
			}, 
			success: function(response, localOpts) {
				var responseData = Ext.decode(response.responseText);
				
				questionForm.response.getComponent('question').update(responseData.question);

				var record = Ext.create('Ext.data.Model', {			
				});
				record.set(responseData);	

				questionForm.loadRecord(record);
				questionForm.evaluationId = evaluationId;
				questionForm.checklistResponse = responseData;

				if (opts && opts.mainForm) {
					questionForm.refreshCallback = opts.mainForm.refreshCallback;
				}		

				//Add change detection
				Ext.defer(function(){
					questionForm.getComponent('tools').getComponent('workflowStatus').on('change', function(field, newValue, oldValue){
						questionForm.saveData();
					}, undefined, {
						buffer: 1000
					});

					questionForm.response.getComponent('score').on('change', function(field, newValue, oldValue){
						questionForm.saveData();
					}, undefined, {
						buffer: 1000
					});
					
					questionForm.response.getComponent('notApplicable').on('change', function(field, newValue, oldValue){
						var scoreField = questionForm.response.getComponent('score');
						if (newValue) {
							scoreField.setValue(null);
							scoreField.setDisabled(true);
						} else {
							scoreField.setDisabled(false);
							questionForm.saveData();	
						}
					}, undefined, {
						buffer: 1000
					});					

					questionForm.response.getComponent('response').on('change', function(field, newValue, oldValue){
						questionForm.saveData();
					}, undefined, {
						buffer: 2000
					});

					questionForm.response.getComponent('privateNote').on('change', function(field, newValue, oldValue){
						questionForm.saveData();
					}, undefined, {
						buffer: 2000
					});					
				}, 1000);
			}
		});
				
		opts.commentPanel.loadComments(evaluationId, "Checklist Question - " + data.question.qid, data.question.questionId);
	},
	saveData: function() {
		var questionForm = this;
		
		var data = questionForm.getValues();
		
		CoreUtil.submitForm({
			url: 'api/v1/resource/evaluations/' + 
				questionForm.evaluationId 
				 + '/checklist/' + 
				 questionForm.checklistResponse.checklistId
				 + '/responses/' + 
				questionForm.checklistResponse.responseId,
			method: 'PUT',
			data: data,
			form: questionForm,
			noLoadmask: true,
			success: function(action, opts) {
				var chkResponse = Ext.decode(action.responseText);
				
				Ext.toast('Saved Response');
				questionForm.getComponent('tools').getComponent('status').setText('Saved at ' + Ext.Date.format(new Date(), 'g:i:s A'));
				
				if (questionForm.refreshCallback) {
					questionForm.refreshCallback(chkResponse);
				}
			}	
		});
			
	}
	
});

