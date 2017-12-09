package developers.sd.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Note {

    private UUID mId;
    private String mTitle = "";
    private String mSubject;
    private String mDate;
    private boolean mSolved;
    private byte[] mImage;
    private String mColor;
    private boolean mDeleted = false;


    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public String getColor() {
        if(mColor!=null) {
            return mColor;
        }
        else
        {
          return "#ffffff";
        }
    }

    public void setColor(String color) {
        mColor = color;
    }

    public byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    public Note(){
        this(UUID.randomUUID());
    }

    public Note(UUID id){
        mId = id;
        mDate = new SimpleDateFormat("MMM dd,yyyy  hh:mm a").format(new Date());
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String Subject) {
        mSubject = Subject;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }


    public boolean isSolved() {
        return mSolved;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getAudioName() { return getId().toString()+".mp3"; }

}
