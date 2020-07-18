package tech.tookan.tookanlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import tech.tookan.tookanlearning.R;
import tech.tookan.tookanlearning.objects.User;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    Context context;
    User[] users;

    public MyRecyclerAdapter(Context c, User[] usersArray) {
        this.context = c;
        this.users = usersArray;
    }

    @NonNull
    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.MyViewHolder holder, int position) {
        User user = users[position];
        Glide.with(context).load(user.getAvatarUrl()).into(holder.image);
        holder.text.setText(user.getLogin());
    }

    @Override
    public int getItemCount() {
        return users.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;

        public MyViewHolder(@NonNull View eachItem) {
            super(eachItem);
            text = eachItem.findViewById(R.id.usernameTextView);
            image = eachItem.findViewById(R.id.userImageView);

        }
    }
}
