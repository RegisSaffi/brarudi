package rw.itdevs.brarudi.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rw.itdevs.brarudi.DetailsActivity;
import rw.itdevs.brarudi.R;
import rw.itdevs.brarudi.model.news;

/**
 * 
 */
public class newsAdapter extends RecyclerView.Adapter<newsAdapter.ViewHolder> {

	Context context;
	voteListener listener;

	private List<news> newsList;

	public newsAdapter(List<news> source, Context context) {

		super();
		this.newsList = source;
		this.context = context;
	}

	public newsAdapter(List<news> source, Context context,voteListener listener) {

		super();
		this.newsList = source;
		this.context = context;
		this.listener=listener;

	}

	@Override
	public int getItemViewType(int position) {

		news pp = newsList.get(position);
		if(pp.getIsVoting()){
			return 0;
		}
		return 1;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if(viewType==0) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false);
			ViewHolder viewHolder = new ViewHolder(v);
			return viewHolder;
		}else{
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout, parent, false);
			ViewHolder viewHolder = new ViewHolder(v);
			return viewHolder;
		}

	}

	@Override
	public void onBindViewHolder(final ViewHolder Viewholder, final int position) {

		final news news1 = newsList.get(position);

		if(!news1.getIsVoting()) {
			Viewholder.title.setText(news1.getTitle());
			Viewholder.desc.setText(news1.getDesc());
			Viewholder.date.setText(news1.getDate());

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
				Viewholder.title.setText(Html.fromHtml(news1.getTitle(),Html.FROM_HTML_MODE_LEGACY));
				Viewholder.desc.setText(Html.fromHtml(news1.getDesc(),Html.FROM_HTML_MODE_LEGACY));
			} else {
				Viewholder.title.setText(Html.fromHtml(news1.getTitle()));
				Viewholder.desc.setText(Html.fromHtml(news1.getDesc()));

			}

			Picasso.with(context)
					.load(news1.getImage())
					.placeholder(R.drawable.loading)
					.into(Viewholder.imgImage);

			Viewholder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent in=new Intent(context, DetailsActivity.class);
					Bundle b=new Bundle();
					b.putString("title",news1.getTitle());
					b.putString("image",news1.getImage());
					b.putString("author",news1.getAuthor());
					b.putString("date",news1.getDate());
					b.putString("url",news1.getUrl());
					b.putString("desc",news1.getDesc());

					in.putExtras(b);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(in);
				}
			});
		}else{

			Viewholder.title.setText(news1.getTitle());

			//Viewholder.radio.setChecked(news1.getIsChecked());

			Picasso.with(context)
					.load(news1.getImage())
					.placeholder(R.drawable.loading)
					.into(Viewholder.imgImage);

			Viewholder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
                        Viewholder.radio.setChecked(false);

						listener.onVote(news1.getTitle(),news1.getImage(),news1.getAuthor());

						for(int a=0;a<newsList.size();a++){
							if(a==position) {
								newsList.get(a).setIsChecked(true);
							}else{
								newsList.get(a).setIsChecked(false);
							}
						}
					}else{
						Viewholder.radio.setChecked(false);
					}
				}
			});

		}


	}

	@Override
	public int getItemCount() {

		return newsList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder{

		TextView title,date;
		TextView desc;
		ImageView imgImage;
		AppCompatRadioButton radio;

		public ViewHolder(View itemView) {

			super(itemView);
			title=  itemView.findViewById(R.id.title);
			desc=  itemView.findViewById(R.id.desc);
			imgImage=  itemView.findViewById(R.id.imgContactProfile);
			date=  itemView.findViewById(R.id.date);
			radio=itemView.findViewById(R.id.radio);

		}
	}

	public interface voteListener{
		public void onVote(String name,String image,String keyword);
	}

}

