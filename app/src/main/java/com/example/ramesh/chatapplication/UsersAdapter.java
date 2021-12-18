package com.example.ramesh.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 11/11/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<UserDetails> userDetails;

    public UsersAdapter(Context context, ArrayList<UserDetails> userDetails) {
        this.context = context;
        this.userDetails = userDetails;
    }
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, final int position) {
        holder.name.setText(userDetails.get(position).getName());
        holder.prof.setText(userDetails.get(position).getProfession());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatWithContact.class);
                intent.putExtra("currentUserId", userDetails.get(position).getUID());
                intent.putExtra("name", userDetails.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDetails==null?0:userDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, prof;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacher_name);
            prof = itemView.findViewById(R.id.teacher_location);
        }
    }
}
