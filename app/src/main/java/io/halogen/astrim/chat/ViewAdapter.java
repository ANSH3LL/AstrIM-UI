package io.halogen.astrim.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import io.halogen.astrim.R;

public class ViewAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_INFORMATION = 3;

    private List<MessageItem> messageList = new ArrayList<>();

    public void addItem(MessageItem msgItem){
        messageList.add(msgItem);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addItemsFromStore(List<MessageItem> msgItems){
        messageList.clear();
        messageList.addAll(msgItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return messageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position){
        MessageItem message = messageList.get(position);
        String sentBy = message.getSenderAlias();
        if(sentBy.equals("$ME!")){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else if(sentBy.equals("$SRV")){
            return VIEW_TYPE_MESSAGE_INFORMATION;
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outgoing, parent, false);
            return new SentMessageHolder(view);
        }
        else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else if(viewType == VIEW_TYPE_MESSAGE_INFORMATION){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_information, parent, false);
            return new InformationMessageHolder(view);
        }
        else{
            return null;
        }
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        MessageItem message = messageList.get(position);
        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_INFORMATION:
                ((InformationMessageHolder) holder).bind(message);
                break;
            default:
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView creationTime;
        EmojiTextView messageBody;

        SentMessageHolder(View itemView){
            super(itemView);
            messageBody = itemView.findViewById(R.id.sentMessage);
            creationTime = itemView.findViewById(R.id.sentTime);
        }

        void bind(MessageItem message){
            messageBody.setText(message.getMsgBody());
            creationTime.setText(message.getCreationTime()); //Utils.formatDateTime(message.getCreatedAt())
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView creationTime;
        EmojiTextView senderAlias, messageBody;

        ReceivedMessageHolder(View itemView){
            super(itemView);
            senderAlias = itemView.findViewById(R.id.senderAlias);
            messageBody = itemView.findViewById(R.id.recvMessage);
            creationTime = itemView.findViewById(R.id.recvTime);
        }

        void bind(MessageItem message){
            senderAlias.setText(message.getSenderAlias());
            messageBody.setText(message.getMsgBody());
            creationTime.setText(message.getCreationTime()); //Utils.formatDateTime(message.getCreatedAt())
        }
    }

    private class InformationMessageHolder extends RecyclerView.ViewHolder {
        TextView messageBody;

        InformationMessageHolder(View itemView){
            super(itemView);
            messageBody = itemView.findViewById(R.id.infoMessage);
        }

        void bind(MessageItem message){
            messageBody.setText(message.getMsgBody());
        }
    }
}
