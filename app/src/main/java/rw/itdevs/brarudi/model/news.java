package rw.itdevs.brarudi.model;
import android.content.Context;
import android.text.style.*;

import rw.itdevs.brarudi.R;

public class news
{

	public String title;
	public String desc;
	public String image="No image";
	String url;
	String author;
	String date;
	Context context;
	boolean isVoting=false;
	boolean checked=false;

    public news(String title,String desc,String image,String url,String author,String date){
        this.title=title;
        this.desc=desc;
        this.image=image;
        this.url=url;
        this.author=author;
        this.date=date;
    }

    public void setIsVoting(boolean voting){this.isVoting=voting;}
    public void setIsChecked(boolean checked){this.checked=checked;}
    public boolean getIsVoting(){return isVoting;}

    public boolean getIsChecked(){return checked;}

    public news(Context context){
        this.context=context;
    }

	public String getTitle()
	{
		return title;
	}

	public String getDesc()
	{
		return desc;
	}
    public String getDate()
    {
        return date;
    }
    public String getAuthor()
    {
        return author;
    }
	public String getUrl()
{
    return url;
}
    public String getImage()
    {
        return image;
    }


}
