import java.applet.*;                                // Applet
import java.awt.*;                                    // Graphics, Image, Color
import java.awt.event.*;

public class Hunsui extends Applet implements Runnable, ActionListener, AdjustmentListener, ItemListener {
    Thread thread = null;                            // スレッド
    int AppletWidth, AppletHeight;                    // アプレットの幅と高さ
    Image WorkImage;                                // 作業用イメージ
    Graphics WorkGraphics;                            // 作業用グラフィックス
    int RunSpeed = 90;                                // 実行スピード

    Scrollbar ScrollBar1, ScrollBar2;                    // スピードバー，角度バー

    AudioClip audio;

    // 各種設定
    int AngleRange = 10;                            // 垂直方向に対しての発射角度幅
    int ShotAngle = 90;
    int FireSpeed = 90;                                // 発射スピードデフォルト値

    int XP, YP;                                        // 発射の位置
    int MAX = 500;                                    // 水滴の最大数
    int X[] = new int[MAX];                        // 現在位置
    int Y[] = new int[MAX];

    double Angle[] = new double[MAX];                // 発射角度（ラジアン)
    double Speed[] = new double[MAX];                // 発射スピード
    double V0x[] = new double[MAX];                // 水平方向の速度
    double V0y[] = new double[MAX];                // 垂直方向の速度
    int Process[] = new int[MAX];                    // プロセス（上昇，落下）
    double Time[] = new double[MAX];                // 経過時間

    double Rad = Math.PI / 180;                        // ラジアン

    // 初期化処理 -----------------------------------------------------------------------
    public void init() {
        AppletWidth = getSize().width;                        // アプレットの幅
        AppletHeight = getSize().height;                    // アプレットの高さ
        WorkImage = createImage(AppletWidth, AppletHeight);    // 作業用イメージ作成
        WorkGraphics = WorkImage.getGraphics();            // 作業用グラフィックス取得

        // 各種パラメータ入力
        RunSpeed = Integer.parseInt(getParameter("runspeed"));         // 実行スピード
        if (RunSpeed > 99) {
            RunSpeed = 99;
        }
        MAX = Integer.parseInt(getParameter("max"));                 // 発射数量
        if (MAX > 500) {
            MAX = 500;
        }
        AngleRange = Integer.parseInt(getParameter("anglerange"));    // 発射角度幅
        FireSpeed = Integer.parseInt(getParameter("firespeed"));    // 発射スピード
        ShotAngle  = Integer.parseInt(getParameter("shotangle")); // 発射角度

        // 初期化
        XP = AppletWidth / 2;                        // 発射の位置
        YP = AppletHeight;
        for (int i = 0; i < MAX; i++) {
            Process[i] = i;                            // 最初だけ順に飛び出すように設定
        }

        // スクロールバー生成
        ScrollBar1 = new Scrollbar(Scrollbar.HORIZONTAL, 10, 1, 0, 90);
        ScrollBar2 = new Scrollbar(Scrollbar.HORIZONTAL, 90, 1, 0, 180);
        ScrollBar1.setBounds(0,  0, AppletWidth, 20); // バーの設定
        ScrollBar2.setBounds(0, 20, AppletWidth, 20); // バーの設定
        add(ScrollBar1);                                // スピードバーをアプレットに付加
        add(ScrollBar2);
        ScrollBar1.addAdjustmentListener(this);
        ScrollBar2.addAdjustmentListener(this);


        audio = Applet.newAudioClip(getClass().getResource("sound.wav"));
    }
    // アプレット開始 -------------------------------------------------------------------
    public void start() {
        thread = new Thread(this);                    // スレッド生成
        thread.start();                            // スレッド開始
    }
    // 描画処理 -------------------------------------------------------------------------
    public void paint(Graphics g) {
        WorkGraphics.setColor(Color.black);            // 描画色を黒色に設定
        WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);    // クリア
        Making();
        g.drawImage(WorkImage, 0, 0, this);            // 作業イメージをアプレットに描画
    }
    // スレッド実行 ---------------------------------------------------------------------
    public void run() {
        while (thread != null) {                        // スレッドが存在している間
            try {
               thread.sleep(1000 - RunSpeed * 10);    // スレッドスリープ
            } catch (InterruptedException e) {}
            repaint();                                // 再描画
        }
    }
    // 描画更新処理再定義 ---------------------------------------------------------------
    public void update(Graphics g) {                // デフォルトのupdateを再定義
        paint(g);                                    // 背景色画面クリア削除，paintのみ
    }
    // アプレット停止 -------------------------------------------------------------------
    public void stop() {
        thread = null;                                // スレッド無効
    }
    // シーン作成 -----------------------------------------------------------------------
    void Making() {
        for (int n = 0; n < MAX; n++) {
            if (Process[n] == 0) {                                // 発射準備段階
                Angle[n] = ShotAngle - AngleRange / 2 + (int)(Math.random()* AngleRange); // 発射角度
                                                                // 発射角度
                Speed[n] = FireSpeed + (int)(Math.random() * 10);    // 発射スピード
                X[n] = AppletWidth / 2;                            // 今回の位置を保管
                Y[n] = AppletHeight;
                V0x[n] = Speed[n] * Math.cos(Angle[n] * Rad);    // 水平方向初速度
                V0y[n] = Speed[n] * Math.sin(Angle[n] * Rad);     // 垂直方向初速度
                Time[n] = 0;                                    // 経過時間クリア
                Process[n] = 1;                                    // 次のプロセス段階
            } else if (Process[n] == 1) {                            // 飛行段階
                if(Y[n] <= AppletHeight) {                        // 表示範囲内
                    Time[n] += 0.1;                                // 時間カウント
                    int xt = (int)(V0x[n] * Time[n]);            // t秒後水平方向の位置
                    int yt = (int)(V0y[n] * Time[n]
                                    - 9.8/2*Time[n]*Time[n]);    // t秒後垂直方向の位置
                    X[n] = XP + xt;                                // t秒後の位置
                    Y[n] = YP - yt;
                    WorkGraphics.setColor(Color.white);
                    WorkGraphics.fillRect(X[n], Y[n], 2, 2);
                } else {
                    Process[n] = 0;                                // 次の段階にセット
                    sound.play();
                }
            } else {
                // 最初だけ順に飛び出すように処理
                Process[n]++;
                if (Process[n] > MAX) {
                    Process[n] = 0;                    // 始めて初期段階とする
                }
            }
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent evt) { // バーの変化キャッチ
        Scrollbar scrollbar = (Scrollbar)evt.getSource();
        if (scrollbar == ScrollBar1) { // 水平バーの場合
            AngleRange = ScrollBar1.getValue();
        } else if (scrollbar == ScrollBar2) { // 垂直バーの場合
            ShotAngle = 180 - ScrollBar2.getValue();
        }
        repaint();
    }
    public void actionPerformed(ActionEvent evt) {}
    public void itemStateChanged(ItemEvent evt) {}
}
