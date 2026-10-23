package com.fanzone.messicr7;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class MainActivity extends Activity {
    static final int PICK_MESSI = 11, PICK_RONALDO = 12;
    SharedPreferences prefs;
    Uri messiAudio, ronaldoAudio;
    MediaPlayer player;
    ArenaView arena;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        prefs = getSharedPreferences("score", MODE_PRIVATE);
        messiAudio = uriPref("messiAudio"); ronaldoAudio = uriPref("ronaldoAudio");
        arena = new ArenaView(); setContentView(arena);
        arena.loadPhotos();
    }
    Uri uriPref(String key) { String s = prefs.getString(key, null); try { return s == null ? null : Uri.parse(s); } catch(Exception e) { return null; } }
    void chooseAudio(int request) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("audio/*");
        startActivityForResult(i, request);
    }
    @Override protected void onActivityResult(int req, int result, Intent data) {
        super.onActivityResult(req,result,data); if(result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri=data.getData(); try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}
        if(req==PICK_MESSI) { messiAudio=uri; prefs.edit().putString("messiAudio",uri.toString()).apply(); }
        if(req==PICK_RONALDO) { ronaldoAudio=uri; prefs.edit().putString("ronaldoAudio",uri.toString()).apply(); }
        arena.invalidate();
    }
    void playAudio(boolean messi) {
        Uri u = messi ? messiAudio : ronaldoAudio;
        if(u==null) { Toast.makeText(this, messi ? "Tap MESSI AUDIO to import the ‘Qué mirás, bobo?’ clip" : "Tap SIUUU AUDIO to import Ronaldo's clip", Toast.LENGTH_LONG).show(); return; }
        try { if(player!=null) { player.release(); player=null; } player = new MediaPlayer(); player.setDataSource(this,u); player.setOnPreparedListener(MediaPlayer::start); player.setOnErrorListener((mp,what,extra)->{ Toast.makeText(this,"Couldn't play this audio file",Toast.LENGTH_SHORT).show(); return true; }); player.prepareAsync(); }
        catch(Exception e) { Toast.makeText(this,"Audio file couldn't be opened",Toast.LENGTH_SHORT).show(); }
    }
    @Override protected void onDestroy() { if(player!=null) player.release(); super.onDestroy(); }

    class ArenaView extends View {
        Paint p = new Paint(3); Random rng = new Random(); Bitmap messi, ronaldo;
        int bg=Color.rgb(7,12,29), cyan=Color.rgb(76,230,255), orange=Color.rgb(255,173,82), gold=Color.rgb(255,213,106), white=Color.WHITE;
        int mWins, rWins, round; boolean running=false, flipping=false, lastMessi=true, resultVisible=false;
        long flipStart=0, nextStart=0; float coinAngle=0; RectF startRect=new RectF(), pauseRect=new RectF(), mAudioRect=new RectF(), rAudioRect=new RectF(), resetRect=new RectF();
        String resultText="READY FOR THE SHOWDOWN";
        ArenaView(){ super(MainActivity.this); mWins=prefs.getInt("mWins",0); rWins=prefs.getInt("rWins",0); round=prefs.getInt("round",0); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }
        void loadPhotos(){ new Thread(()->{ try { messi=download("https://commons.wikimedia.org/wiki/Special:Redirect/file/Lionel_Messi_Argentina_v_Spain_19_July_2026-076.jpg?width=900"); }catch(Exception ignored){} try { ronaldo=download("https://commons.wikimedia.org/wiki/Special:Redirect/file/Cristiano_Ronaldo_Croatia_v_Portugal_2_July_2026-075_%28cropped%29.jpg?width=900"); }catch(Exception ignored){} postInvalidate(); }).start(); }
        Bitmap download(String u) throws Exception { URL url=new URL(u); java.net.HttpURLConnection c=(java.net.HttpURLConnection)url.openConnection(); c.setConnectTimeout(10000); c.setReadTimeout(15000); c.setRequestProperty("User-Agent","MessiCR7CoinToss/1.0"); c.connect(); InputStream in=c.getInputStream(); Bitmap b=BitmapFactory.decodeStream(in); in.close(); c.disconnect(); return b; }
        void text(Canvas c,String s,float x,float y,float size,int color,boolean bold,Paint.Align align){ p.setColor(color);p.setTextSize(size);p.setTypeface(bold?Typeface.create("sans-serif-black",Typeface.BOLD):Typeface.create("sans-serif-medium",Typeface.NORMAL));p.setTextAlign(align);p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p); }
        void roundRect(Canvas c,RectF r,float rad,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRoundRect(r,rad,rad,p);}
        void strokeRect(Canvas c,RectF r,float rad,int color,float sw){p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sw);c.drawRoundRect(r,rad,rad,p);p.setStyle(Paint.Style.FILL);}
        @Override protected void onDraw(Canvas raw){ super.onDraw(raw); float sx=getWidth()/360f, sy=getHeight()/640f; raw.save(); raw.scale(sx,sy); Canvas c=raw;
            p.setShader(new LinearGradient(0,0,360,640,new int[]{Color.rgb(8,14,35),Color.rgb(14,24,48),Color.rgb(5,9,22)},null,Shader.TileMode.CLAMP));c.drawRect(0,0,360,640,p);p.setShader(null);
            // Stadium spotlights and pitch lines
            p.setShader(new RadialGradient(180,260,220,new int[]{0x3346DFFF,0x0046DFFF},null,Shader.TileMode.CLAMP));c.drawRect(0,0,360,600,p);p.setShader(null);
            p.setColor(0x1CFFFFFF);p.setStrokeWidth(1);c.drawOval(-45,205,405,600,p);c.drawLine(0,465,360,465,p);
            text(c,"THE GOAT FLIP",180,32,11,0xFF9CAFCB,true,Paint.Align.CENTER); text(c,"M10  VS  CR7",180,58,25,white,true,Paint.Align.CENTER);
            // score panels
            RectF left=new RectF(14,78,170,194), right=new RectF(190,78,346,194);
            roundRect(c,left,17,0xFF111D34);roundRect(c,right,17,0xFF111D34);strokeRect(c,left,17,orange,1.2f);strokeRect(c,right,17,cyan,1.2f);
            drawPortrait(c,ronaldo,left,orange,"CR7"); drawPortrait(c,messi,right,cyan,"M10");
            text(c,"RONALDO",92,157,12,white,true,Paint.Align.CENTER);text(c,"MESSI",268,157,12,white,true,Paint.Align.CENTER);
            text(c,String.format(java.util.Locale.US,"%04d",rWins),92,183,24,orange,true,Paint.Align.CENTER);text(c,String.format(java.util.Locale.US,"%04d",mWins),268,183,24,cyan,true,Paint.Align.CENTER);
            text(c,"WINS",92,191,7,0xFF8B9BB5,true,Paint.Align.CENTER);text(c,"WINS",268,191,7,0xFF8B9BB5,true,Paint.Align.CENTER);
            // central coin stage
            p.setColor(0x332DDBFF);c.drawCircle(180,324,94,p);p.setColor(0x224D6B9C);c.drawCircle(180,324,78,p);
            float angle=flipping?(System.currentTimeMillis()-flipStart)/1000f*17.5f:0; float sxCoin=flipping?(float)Math.abs(Math.cos(angle)):1f; sxCoin=Math.max(.12f,sxCoin);
            c.save();c.translate(180,324);c.scale(sxCoin,1f);
            p.setShader(new LinearGradient(-62,-62,60,70,new int[]{gold,Color.rgb(196,126,32),Color.rgb(255,232,151)},null,Shader.TileMode.CLAMP));c.drawCircle(0,0,63,p);p.setShader(null);
            p.setColor(0xFF6B471C);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(4);c.drawCircle(0,0,56,p);p.setStyle(Paint.Style.FILL);
            boolean showM=flipping?(((int)(angle/Math.PI)&1)==0):lastMessi;
            text(c,showM?"M10":"CR7",0,-1,30,Color.rgb(27,34,48),true,Paint.Align.CENTER);text(c,showM?"MESSI":"RONALDO",0,24,10,Color.rgb(27,34,48),true,Paint.Align.CENTER);
            c.restore();
            text(c,flipping?"FLIPPING...":resultText,180,414,14,flipping?gold:(lastMessi?cyan:orange),true,Paint.Align.CENTER);
            text(c,"ROUND  "+String.format(java.util.Locale.US,"%06d",round),180,437,10,0xFF8FA1BD,true,Paint.Align.CENTER);
            // streak / percentages
            int total=mWins+rWins; float mp=total==0?50f:(100f*mWins/total), rp=100f-mp;
            roundRect(c,new RectF(25,454,335,461),4,0xFF26334C);roundRect(c,new RectF(25,454,25+310*mp/100f,461),4,cyan);
            text(c,String.format(java.util.Locale.US,"MESSI %.1f%%",mp),26,478,9,cyan,true,Paint.Align.LEFT);text(c,String.format(java.util.Locale.US,"RONALDO %.1f%%",rp),334,478,9,orange,true,Paint.Align.RIGHT);
            startRect.set(20,499,174,548); pauseRect.set(186,499,340,548); resetRect.set(137,558,223,584);
            roundRect(c,startRect,14,cyan);text(c,running?"RUNNING":"▶  START",97,529,14,bg,true,Paint.Align.CENTER);
            roundRect(c,pauseRect,14,0xFF202D45);strokeRect(c,pauseRect,14,0xFF52627C,1);text(c,running?"Ⅱ  PAUSE":"▶  RESUME",263,529,13,white,true,Paint.Align.CENTER);
            roundRect(c,resetRect,9,0xFF18243A);strokeRect(c,resetRect,9,0xFF40506A,1);text(c,"↻ RESET",180,575,9,0xFFB4C2D8,true,Paint.Align.CENTER);
            mAudioRect.set(16,594,173,626);rAudioRect.set(187,594,344,626);
            roundRect(c,mAudioRect,10,0xFF10283B);strokeRect(c,mAudioRect,10,cyan,1);roundRect(c,rAudioRect,10,0xFF302219);strokeRect(c,rAudioRect,10,orange,1);
            text(c,messiAudio==null?"♫ ADD MESSI AUDIO":"♫ MESSI AUDIO ✓",94,614,9,cyan,true,Paint.Align.CENTER);text(c,ronaldoAudio==null?"♫ ADD SIUUU AUDIO":"♫ SIUUU AUDIO ✓",265,614,9,orange,true,Paint.Align.CENTER);
            text(c,"PHOTO: WIKIMEDIA COMMONS • CC BY-SA 4.0",180,636,6,0xFF66748B,false,Paint.Align.CENTER);
            raw.restore();
            if(flipping){ long elapsed=System.currentTimeMillis()-flipStart; if(elapsed>=3600){finishFlip();} else {postInvalidateDelayed(16);} }
            else if(running && nextStart>0 && System.currentTimeMillis()>=nextStart){beginFlip();} else if(running && nextStart>0){postInvalidateDelayed(Math.min(100,nextStart-System.currentTimeMillis()));}
        }
        void drawPortrait(Canvas c,Bitmap b,RectF panel,int accent,String fallback){ float cx=panel.centerX(),cy=panel.top+37; p.setColor(0xFF26364F);c.drawCircle(cx,cy,27,p);p.setColor(accent);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);c.drawCircle(cx,cy,28,p);p.setStyle(Paint.Style.FILL);
            if(b!=null){int save=c.save();Path path=new Path();path.addCircle(cx,cy,26,Path.Direction.CW);c.clipPath(path);Rect src=new Rect(0,0,b.getWidth(),b.getHeight());RectF dst=new RectF(cx-26,cy-26,cx+26,cy+26);p.setColor(white);c.drawBitmap(b,src,dst,p);c.restoreToCount(save);}else{text(c,fallback,cx,cy+7,16,accent,true,Paint.Align.CENTER);} }
        void beginFlip(){ flipping=true;resultVisible=false;flipStart=System.currentTimeMillis();nextStart=0;invalidate(); }
        void finishFlip(){ if(!flipping)return;flipping=false;lastMessi=rng.nextBoolean();round++; if(lastMessi){mWins++;resultText="QUÉ MIRÁS, BOBO?";playAudio(true);}else{rWins++;resultText="SIUUUU!";playAudio(false);}prefs.edit().putInt("mWins",mWins).putInt("rWins",rWins).putInt("round",round).apply();nextStart=running?System.currentTimeMillis()+500:0;invalidate(); }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=e.getX()*360/getWidth(),y=e.getY()*640/getHeight();
            if(startRect.contains(x,y)){ if(!running){running=true;if(!flipping)beginFlip();} else { if(!flipping)beginFlip(); } invalidate();return true; }
            if(pauseRect.contains(x,y)){running=!running;if(running&&!flipping){beginFlip();}else if(!running)nextStart=0;invalidate();return true;}
            if(mAudioRect.contains(x,y)){chooseAudio(PICK_MESSI);return true;}if(rAudioRect.contains(x,y)){chooseAudio(PICK_RONALDO);return true;}
            if(resetRect.contains(x,y)){mWins=0;rWins=0;round=0;resultText="READY FOR THE SHOWDOWN";lastMessi=true;running=false;flipping=false;nextStart=0;prefs.edit().putInt("mWins",0).putInt("rWins",0).putInt("round",0).apply();invalidate();return true;}return true;
        }
    }
}
