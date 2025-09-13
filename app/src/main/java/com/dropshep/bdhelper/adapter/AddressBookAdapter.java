package com.dropshep.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.ModelAddressBook;

import java.util.ArrayList;

public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.HolderViewAddressBook>{

    Context context;
    ArrayList<ModelAddressBook> addressBookArrayList;

    private static OnItemClickListener mListener;

    public AddressBookAdapter(Context context, ArrayList<ModelAddressBook> addressBookArrayList) {
        this.context = context;
        this.addressBookArrayList = addressBookArrayList;
    }

    @NonNull
    @Override
    public HolderViewAddressBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater
        View view = LayoutInflater.from(context).inflate(R.layout.row_address_book_item, parent, false);
        return new HolderViewAddressBook(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewAddressBook holder, int position) {
        //Get Data
        ModelAddressBook modelAddressBook = addressBookArrayList.get(position);
        String addressName = modelAddressBook.getAddressName();
        String address = modelAddressBook.getAddress();
        String recipientName = modelAddressBook.getRecipientName();
        String recipientMobile = modelAddressBook.getRecipientMobile();


        holder.addressNameTv.setText(addressName);
        holder.addressTv.setText(address);
        holder.recipientNameTv.setText(recipientName);
        holder.recipientMobileTv.setText(recipientMobile);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null){
                mListener.onItemClick(v, holder.getAdapterPosition());
            }
        });


        holder.moreBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    if (mListener != null) mListener.onShowItemClick(holder.getAdapterPosition());
                    return true;
                } else if (item.getTitle().equals("Delete")) {
                    if (mListener != null) mListener.onDeleteItemClick(holder.getAdapterPosition());
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return addressBookArrayList.size();
    }

    static class HolderViewAddressBook extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView addressNameTv, addressTv, recipientNameTv, recipientMobileTv;
        ImageButton moreBtn;
        public HolderViewAddressBook(@NonNull View itemView) {
            super(itemView);
            //init views
            addressNameTv = itemView.findViewById(R.id.addressNameTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            recipientNameTv = itemView.findViewById(R.id.recipientNameTv);
            recipientMobileTv = itemView.findViewById(R.id.recipientMobileTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);

            moreBtn.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(v, position);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
