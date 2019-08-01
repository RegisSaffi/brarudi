package rw.itdevs.brarudi.model;
import android.content.Context;

import rw.itdevs.brarudi.R;

public class cal
{

	public String session;
	public String speaker;
	public String time="No image";
	public String venue="No image";
	String date;


	public String getSession()
	{
		return session;
	}
	public void setSession(String session)
	{
		this.session=session;
	}

	public String getSpeaker()
	{
		return speaker;
	}
	public void setSpeaker(String speaker)
	{
		this.speaker=speaker;
	}

	public String getTime()
	{
		return time;
	}
	public void setTime(String time)
	{
		this.time=time;
	}

	public String getVenue()
	{
		return venue;
	}
	public void setVenue(String venue)
	{
		this.venue=venue;
	}

	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date=date;
	}

}
