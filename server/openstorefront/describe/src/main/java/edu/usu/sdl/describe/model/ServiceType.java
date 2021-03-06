/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usu.sdl.describe.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 *
 * @author dshurtleff
 */
@Root(strict = false, name = "type")
public class ServiceType
{
	@Attribute(required = false)
	private String style;
	
	@Attribute(required = false)
	private String version;
	
	@Attribute(required = false)
	private String secure;
	
	@Text(required = false)
	private String text;

	public ServiceType()
	{
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getSecure()
	{
		return secure;
	}

	public void setSecure(String secure)
	{
		this.secure = secure;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

}
