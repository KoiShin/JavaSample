import java.applet.*;                       // Applet
import java.awt.*;                          // Graphics, Image, Color, Font, FontMetrics
import java.util.*;

public class ElectricBulletinBoard extends Applet implements Runnable {
    Image WorkImage;                                // 作業用イメージ
    Graphics WorkGraphics;                          // 作業用グラフィックス
    int AppletWidth, AppletHeight;                  // アプレットの幅と高さ
    String Message[ ] = new String[10];             // メッセージ　Max10
    int MessageNumber,                              // メッセージの数
        MessageLength,                              // メッセージの長さ
        MessageCount;                               // メッセージ表示カウント
    int Step,                                       // メッセージの移動間隔
        Mesh,                                       // メッシュ間隔
        DispPoint,                                  // メッセージの表示水平位置
        height_;
    String FontName;                                // フォント名
    Font font;                                      // フォント
    FontMetrics fm;                                 // フォントメトリックス
    boolean PrepareFlag = false;                    // 準備フラグ
    Thread thread = null;                           // スレッド

    // 初期化処理 -----------------------------------------------------------------------
    public void init( ) {
        AppletWidth = getSize( ).width;                     // アプレットの幅と高さ
        AppletHeight = getSize( ).height;
        WorkImage = createImage(AppletWidth, AppletHeight); // 作業用イメージ作成
        WorkGraphics = WorkImage.getGraphics( );            // 作業用グラフィックス取得
        FontName = getParameter("font");                    // フォント名設定
        font = new Font(FontName, Font.BOLD, AppletHeight * 8 / 10);
        fm = getFontMetrics(font);                          // フォントメトリックス取得
        MessageNumber = Integer.parseInt(getParameter("number"));// メッセージ数
        for (int i = 0; i < MessageNumber; i++)
            Message[i] = getParameter("message" + i);       // メッセージ取得
        Step = Integer.parseInt(getParameter("step"));      // メッセージの移動間隔
        Mesh = Integer.parseInt(getParameter("mesh"));      // メッシュ間隔
    }
    // アプレット開始 -------------------------------------------------------------------
    public void start( ) {
        thread = new Thread(this);                  // スレッド生成
        thread.start( );                            // スレッドスタート
    }
    // 描画処理 -------------------------------------------------------------------------
    public void paint(Graphics g) {
        if (PrepareFlag == false)                   // 準備ができていない場合
            return;
        // 作業グラフィックに描画
        WorkGraphics.setColor(Color.black);                     // 背景の色設定
        WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight); // 背景描画
        WorkGraphics.setFont(font);                             // フォント設定
        WorkGraphics.setColor(new Color(0, 255, 0));            // メッセージの色設定
        WorkGraphics.drawString(Message[MessageCount],          // メッセージ描画
                                DispPoint, height_);
        WorkGraphics.setColor(new Color(0, 50, 0));             // メッシュの色設定
         for (int x = 0; x < AppletWidth; x += Mesh)                // 縦にメッシュを入れる
            WorkGraphics.fillRect(x, 0, 1, AppletHeight);
         for (int y = 0; y < AppletHeight; y += Mesh)           // 横にメッシュを入れる
            WorkGraphics.fillRect(0, y, AppletWidth, 1);
        g.drawImage(WorkImage, 0, 0, this);         // 作業イメージをアプレットに描画
    }
    // スレッド実行 ---------------------------------------------------------------------
    public void run( ) {
        height_ = AppletHeight * 8 / 10;
        // 表示番号と表示位置の初期設定
        MessageCount = 0;                           // メッセージ表示カウントクリア
        DispPoint = AppletWidth;                    // 表示位置を右端に設定
        MessageLength = fm.stringWidth(Message[MessageCount]); // メッセージの長さ
        PrepareFlag = true;                         // 準備完了
        while (thread != null) {                    // スレッドが存在している間
            if (MessageCount % 3 == 0) {
                for (int i = 0; i < 3; i++) {
                    try {
                        DispPoint = 0;
                        repaint();
                        Thread.sleep(100);
                        DispPoint = AppletWidth + 1;
                        repaint();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            } else if (MessageCount % 3 == 1) {
                DispPoint = 0;
                for (height_ = AppletHeight + AppletHeight * 2 / 3 + 1; height_ >= -1 * (AppletHeight / 3 + 1); height_--) {
                    try {
                        repaint();
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                }
                height_ = AppletHeight * 8 / 10;
            } else if (MessageCount % 3 == 2) {
                DispPoint = AppletWidth;            // 表示位置を右端にセット
                MessageLength = fm.stringWidth(Message[MessageCount]);// メッセージの長さ

                for (; DispPoint >= -MessageLength; DispPoint -= Step) {
                    try {
                        repaint();
                        thread.sleep(50);
                    } catch (InterruptedException e) {}
                }
            }

            MessageCount++;
            if (MessageCount == MessageNumber)
                Message[MessageCount] = NowTime();
            if (MessageCount > MessageNumber)   // メッセージ数を超えた場合
                MessageCount = 0;               // 最初のメッセージ
        }
    }

    String NowTime() {
        String weeks[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        String week = weeks[date.get(Calendar.DAY_OF_WEEK) - 1];
        String hour = "0" + date.get(Calendar.HOUR);
        hour = hour.substring(hour.length() - 2);
        String minute = "0" + date.get(Calendar.MINUTE);
        minute = minute.substring(minute.length() - 2);
        return year + "/" + month + "/" + day + "(" + week + ")" + hour + ":" + minute;
    }
    // 描画更新処理再定義 ---------------------------------------------------------------
    public void update(Graphics g) {                // デフォルトのupdateを再定義
        paint(g);                                   // 背景色画面クリア削除，paintのみ
    }
    // アプレット停止 -------------------------------------------------------------------
    public void stop( ) {
        thread = null;                              // スレッド無効
    }
}
