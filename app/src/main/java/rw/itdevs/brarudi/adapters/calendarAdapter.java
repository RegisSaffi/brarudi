package rw.itdevs.brarudi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import rw.itdevs.brarudi.R;
import rw.itdevs.brarudi.model.cal;

/**
 * 
 */
public class calendarAdapter extends RecyclerView.Adapter<calendarAdapter.ViewHolder> {

	Context context;

	private List<cal> calList;

	public calendarAdapter(List<cal> source, Context context) {

		super();
		this.calList = source;
		this.context = context;

	}

	@Override
	public int getItemViewType(int position) {

		cal pp = calList.get(position);
		return 0;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_item, parent, false);
			ViewHolder viewHolder = new ViewHolder(v);

			return viewHolder;

	}

	@Override
	public void onBindViewHolder(ViewHolder Viewholder, int position) {

		final cal cal1 = calList.get(position);

		Viewholder.session.setText(cal1.getSession());
		Viewholder.speaker.setText(cal1.getSpeaker());

		Viewholder.time.setText(cal1.getTime());
		Viewholder.venue.setText(cal1.getVenue());

		Viewholder.date1.setText(cal1.getDate().substring(0,2));
		Viewholder.date2.setText(cal1.getDate().substring(2));

		Viewholder.date1.setTextColor(getRandomColor());

	}

	public int getRandomColor(){
		int[] colors = context.getResources().getIntArray(R.array.colors);
		Random rand=new Random();

		int i=rand.nextInt(colors.length);
		return colors[i]/2;
	}


	@Override
	public int getItemCount() {

		return calList.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder{

		TextView session,speaker,time,venue,date1,date2;

		public ViewHolder(View itemView) {

			super(itemView);
			session=  itemView.findViewById(R.id.session);
			speaker=  itemView.findViewById(R.id.speaker);
			time=  itemView.findViewById(R.id.time);

			venue=  itemView.findViewById(R.id.venue);
			date1=  itemView.findViewById(R.id.date1);
			date2=  itemView.findViewById(R.id.date2);

		}
	}

}

