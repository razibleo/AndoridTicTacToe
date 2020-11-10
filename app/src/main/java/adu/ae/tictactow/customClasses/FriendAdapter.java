package adu.ae.tictactow.customClasses;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import adu.ae.tictactow.R;
import adu.ae.tictactow.utils.Friend;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private ArrayList<Friend> friendArrayList;

    public FriendAdapter(ArrayList<Friend> friendArrayList) {
        this.friendArrayList = friendArrayList;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        ImageView friendImageView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendImageView = itemView.findViewById(R.id.friendImageView);
        }
    }


    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_recycler_view,viewGroup,false);
        FriendViewHolder friendViewHolder = new FriendViewHolder(view);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder friendViewHolder, int i) {
        Friend friend = friendArrayList.get(i);
        friendViewHolder.friendImageView.setImageResource(friend.getImageResourceID());

    }

    @Override
    public int getItemCount() {
        return friendArrayList.size();
    }
}
