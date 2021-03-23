package com.mrhi.mp3;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.mrhi.mp3.MainActivity.viewPager2;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    public static ArrayList<MusicData> musicList;
    public static int selectedPosition;
    public static boolean likeFlag = false;

    //2. 생성자 생성
    public MusicAdapter(Context context, ArrayList<MusicData> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public static MusicData getSelectedMusic(){
        return musicList.get(selectedPosition);
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

    //데이터를 제공
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {
        //음악의 전체 재생시간
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

        MusicData musicData = musicList.get(position);

        //앨범자켓을 비트맵으로 만들기
        if (musicData.getAlbumArt() != null) {
            Bitmap albumImg = getAlbumImg(context, Integer.parseInt(musicData.getAlbumArt()), 200);
            if (albumImg != null) {
                customViewHolder.albumArt.setImageBitmap(albumImg);
            }
        } else {
            customViewHolder.albumArt.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.album));
        }

        // recyclerviewer에 보여줘야할 정보 세팅
        customViewHolder.title.setText(musicData.getTitle());
        customViewHolder.artist.setText(musicData.getArtist());
        if (musicData.getDuration() != 0) {
            customViewHolder.duration.setText(simpleDateFormat.format(musicData.getDuration()));
        } else {
            customViewHolder.duration.setText("04:12");
        }

        Log.d("MusicAdapter", "error");

        if (true == likeFlag && musicData.getLiked() == false) {
            customViewHolder.linearLayout.setVisibility(View.GONE);
        } else {
            customViewHolder.linearLayout.setVisibility(View.VISIBLE);
        }
    }

    //앨범아트를 content provider로 가져오는 함수
    public static Bitmap getAlbumImg(Context context, int albumArt, int imgMaxSize) {
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

    //1. 내부클래스 뷰홀더를 생성
    //값만 바꿔서 밑으로 보냄 어차피 화면에는 한정적으로 보이니까
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView albumArt;
        private TextView title, artist, duration;

        public CustomViewHolder(@NonNull View itemView) {   //밑으로 내려서 사라진 뷰가 itemView로 옴
            super(itemView);

            this.linearLayout = itemView.findViewById(R.id.linearLayout);
            this.albumArt = itemView.findViewById(R.id.d_ivAlbum);
            this.title = itemView.findViewById(R.id.d_tvTitle);
            this.artist = itemView.findViewById(R.id.d_tvArtist);
            this.duration = itemView.findViewById(R.id.d_tvDuration);

            //2-4. 구현
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = getAdapterPosition();

                    //넘겨주는 수단
                    viewPager2.setCurrentItem(2);
                }
            });//end of setOnClickListener
        }
    }//end of CustomViewHolderClass

    //사진을 Bitmap를 String으로 바꿔주는
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

}//end of MusicAdapterClass
