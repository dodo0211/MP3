package com.mrhi.mp3;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> musicList;

    //2-2. 내부 인터페이스 멤버변수 생성
    //리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    //2. 생성자 생성
    public MusicAdapter(Context context, ArrayList<MusicData> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //3. 화면 객체를 가져와서 viewHolder에 저장한다.
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);

        //위에서 만든 view를 CustomViewHolder에 넘겨준다 -> 속도개선
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    //데이터를 제공하는 것
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {
        //음악의 전체 재생시간
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        //앨범자켓을 비트맵으로 만들기
        if(musicList.get(position).getAlbumArt() != null){
            Bitmap albumImg = getAlbumImg(context, Integer.parseInt(musicList.get(position).getAlbumArt()), 200);
            if(albumImg != null){
                customViewHolder.albumArt.setImageBitmap(albumImg);
            }
        }else{
            customViewHolder.albumArt.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.album));
        }

        // recyclerviewer에 보여줘야할 정보 세팅
        customViewHolder.title.setText(musicList.get(position).getTitle());
        customViewHolder.artist.setText(musicList.get(position).getArtist());
        if(musicList.get(position).getDuration() != null){
            customViewHolder.duration.setText(sdf.format(Integer.parseInt(musicList.get(position).getDuration())));
        }else{
            customViewHolder.duration.setText("04:12");
        }

        Log.d("MusicAdapter", "error");
    }

    //앨범아트를 content provider로 가져오는 함수
    public Bitmap getAlbumImg(Context context, int albumArt, int imgMaxSize) {
            /*컨텐트프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
                특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
                무조건 컨텐트프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
                다른 앱의 데이터를 사용하고자 하는 앱에서는 Uri를 이용하여 컨텐트리졸버(Content Resolver)를 통해
                다른 앱의 컨텐트프로바이더에게 데이터를 요청하게 되는데
                요청받은컨텐트프로바이더는 Uri를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
            */
        BitmapFactory.Options options = new BitmapFactory.Options();

        //다른 앱에 있는 provider에게 정보를 요청
        ContentResolver contentResolver = context.getContentResolver();

        //uri방식으로 요구
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumArt);

        if(uri != null){
            ParcelFileDescriptor fd = null;
            try {
                fd = contentResolver.openFileDescriptor(uri, "r");   //contentResolver에 요청

                //메모리 할당을 하지 않으면서 해당된 정보를 읽어올 수 있음
                options.inJustDecodeBounds = true;
                int scale = 0;

                //사진 크기 맞추기(200)
                if(options.outHeight > imgMaxSize || options.outWidth > imgMaxSize){
                    scale = (int)Math.pow(2,(int) Math.round(Math.log(imgMaxSize /
                            (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }

                //비트맥을 위해서 메모리를 할당하겠다.
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if(bitmap != null){
                    //bitmap image가 최대 200보다 넘어버리면
                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;

            } catch (FileNotFoundException e) {
                Log.d("MusicAdapter", "contentResolver error");
            }finally {
                if(fd != null){
                    try {
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }//end of finally
        }//end of if
        return null;
    }//end of getAlbumImgFunc

    //전체갯수 확인
    @Override
    public int getItemCount() {
        return (musicList != null)? musicList.size() : 0;
    }

    //1. 내부클래스 뷰홀더를 만든다.
    //값만 바꿔서 밑으로 보냄 어차피 화면에는 한정적으로 보이니까
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumArt;
        private TextView title, artist, duration;

        public CustomViewHolder(@NonNull View itemView) {   //밑으로 내려서 사라진 뷰가 itemView로 옴
            super(itemView);

            this.albumArt = itemView.findViewById(R.id.d_ivAlbum);
            this.title = itemView.findViewById(R.id.d_tvTitle);
            this.artist = itemView.findViewById(R.id.d_tvArtist);
            this.duration = itemView.findViewById(R.id.d_tvDuration);

            //2-4. 구현
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION);{
                        mListener.onItemClick(view, position);
                    }
                }
            });//end of setOnClickListener
        }
    }//end of CustomViewHolderClass

    //2-1. 내부 인터페이스를 정의한다.
    public interface OnItemClickListener{
        //추상화메소드 구현
        void onItemClick(View view , int position);

    }//end of InItemClickListener

    //2-3. 내부 인터페이스 멤버변수에 대한 setter 생성
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}//end of MusicAdapterClass
