import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color
import java.awt.event.*;							// ActionListener, ActionEvent
import java.util.*;									// Calendar, TimeZone

public class MonthCalendar2 extends Applet
			 		implements ActionListener {	// ActionListenerインターフェースを実装

	int Year, Month;								// 年・月
	Button  LastYearButton,							// 昨年のこの月描画ボタン
			LastMonthButton,						// 前月描画ボタン
			NextYearButton,							// 翌年のこの月描画ボタン
			NextMonthButton;						// 次の月描画ボタン

	int Holiday[ ] = {
		 101, // 元旦
		 100, // 成人の日	------- 第2月曜日
		 211, // 建国記念日
		 300, // 春分の日	------- 計算
		 429, // みどりの日
		 503, // 憲法記念日
		 504, // 国民の休日
		 505, // 子供の日
		 720, // 海の日		------- 第3月曜日
		 915, // 敬老の日
		 900, // 秋分の日	------- 計算
		1000, // 体育の日	------- 第2月曜日
		1103, // 文化の日
		1123, // 勤労感謝の日
		1223  // 天皇誕生日
		};

	// 初期化処理 -----------------------------------------------------------------------
	public void init( ) {
		setLayout(null);							// レイアウトを自由設定にする

		LastYearButton = new Button("<<");			// 前年ボタン生成
		NextYearButton = new Button(">>");			// 次年ボタン生成
		LastMonthButton = new Button("<");			// 前月ボタン生成
		NextMonthButton = new Button(">");			// 次月ボタン生成
		add(LastYearButton);						// 各ボタンをアプレットに付加
		add(NextYearButton);
		add(LastMonthButton);
		add(NextMonthButton);

		LastYearButton.addActionListener(this);		// 各ボタンにリスナー追加
		NextYearButton.addActionListener(this);
		LastMonthButton.addActionListener(this);
		NextMonthButton.addActionListener(this);

		LastYearButton.setBounds(10, 5, 30, 30);	// ボタンの配置とサイズ設定
		NextYearButton.setBounds(280, 5,30, 30);	// setBounds (x, y, w, h)
		LastMonthButton.setBounds(50, 5, 30, 30);
		NextMonthButton.setBounds(240, 5,30, 30);

		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		Year = date.get(Calendar.YEAR);		 		// 現在の年取得
		Month = date.get(Calendar.MONTH) + 1;		// 現在の月取得 (0:1月，1：2月，〜，11:12月）
	}
	// 描画処理 -------------------------------------------------------------------------
	public void paint(Graphics g) {
		int Days[ ] = { 0, 31, 28, 31, 30,   31, 30, 31, 31,  30, 31, 30, 31 };
        String week[ ] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

		if ((Year % 4 == 0 && Year % 100 != 0) || Year % 400 == 0)	// 閏年
			Days[2] = 29;							// 2月の日数修正
		else
 			Days[2] = 28;							// 平年

		// 背景表示
		g.setColor(new Color(235, 150, 0));
		g.fillRect(0, 0, 320, 240);					// 背景枠部分
		g.setColor(new Color(255, 255, 255));
		g.fillRect(17, 55, 287, 175);				// カレンダー表示部
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, 319, 239);					// 外枠

		// 影付きで，年／月表示
		for (int i = 0; i <= 1; i++) {
			if (i == 0)								// i=0のときに黒色で影表示
				g.setColor(Color.black);
			else									// i=1のときに白色で前面部分表示
				g.setColor(Color.white);

			Font f = new Font("Courier", Font.BOLD, 30);	// フォント定義
			g.setFont(f);									// フォント設定

			if (Month < 10)							// 1桁の場合，表示位置設定
				g.drawString(Year + "/"  + Month, 107 - i, 30);
			else									// 2桁の場合
				g.drawString(Year + "/"  + Month, 100 - i, 30);
		}

		// 曜日描画
		g.setColor(Color.black);
		Font f = new Font("TimesRoman", Font.PLAIN, 12);	// フォント定義
		g.setFont(f);										// フォント設定
		for (int i = 0; i < 7; i++)
			g.drawString(week[i], 33 + i * 40, 50);

		// 枠作成
		g.setColor(new Color(255, 230, 0));
		for (int i = 0; i <= 5; i++)
			for (int j = 1; j <= 7; j++)
				g.drawRect(j * 40 - 20, i * 28 + 58, 40, 28);

		// 表示フォント設定
		f = new Font("Courier", Font.BOLD, 30);
		g.setFont(f);

		// 曜日計算
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(Year, Month - 1, 1);				// set(Year, Month, Date)
													// 月だけは，0:1月,1:2月,・,11:12月
		int col = date.get(Calendar.DAY_OF_WEEK);	// 曜日 1:日，2:月，3:火，・・・，7:土

		// カレンダーの日作成
		HolidaySyori( );								// 休日処理					
		int row = 1;
		int hurikae = 0;								// 振替クリア				
		for (int day = 1; day <= Days[Month]; day++) {
			if (col == 1 || hurikae == 1)				// 日曜日かまたは振替の場合	
				g.setColor(Color.red);
			else if (col == 7)							// 土曜日の場合
				g.setColor(Color.blue);
			else										// 平日の場合
				g.setColor(Color.black);

			// 祝日　＆　振替休日													
			hurikae = 0;								// 振替クリア				
			for (int h = 0; h < Holiday.length; h++) {	// 祝日チェック				
				if (Month * 100 + day == Holiday[h]) {	// 祝日の場合				
					g.setColor(Color.red);											
					if (col == 1)						// 日曜の場合				
						hurikae = 1;					// 振替セット				
					break;															
				}																	
			}																		

			if (day < 10)								// 日付が1桁の場合
				g.drawString(day + " ", col * 40 - 5, row * 28 + 55);
			else										// 日付が2桁の場合
				g.drawString(day + " ", col * 40 - 15, row * 28 + 55);
			col++;
			if (col > 7) {
				row++;									// 次の行へ
				col = 1;								// 曜日を日曜日に
			}
		}

	}
	// 休日処理 -------------------------------------------------------------------------
	void HolidaySyori( ) {
		// 成人の日　1月の第2月曜
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(Year, 1 - 1, 1);				 	// 1月1日にセット
		int col = date.get(Calendar.DAY_OF_WEEK);	// 曜日計算 1:日，2:月，3:火，・・7:土
		int count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// 月曜日の場合
				count++;
				if (count == 2) {					// 第2月曜日
					Holiday[1] = 1 * 100 + c;
					break;
				}
			}
			col++;									// 次の曜日にセット
			if (col > 7)							// 土曜日の次は
				col = 1;							// 日曜日
		}
		// 海の日　７月の第3月曜
		date.set(Year, 7 - 1, 1);					// 7月1日にセット
		col = date.get(Calendar.DAY_OF_WEEK);		// 曜日計算 1:日，2:月，3:火，・・7:土
		count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// 月曜日の場合
				count++;
				if (count == 3) {					// 第3月曜日
					Holiday[8] = 7 * 100 + c;
					break;
				}
			}
			col++;									// 次の曜日にセット
			if (col > 7)							// 土曜日の次は
				col = 1;							// 日曜日
		}
		// 体育の日　10月の第2月曜
		date.set(Year, 10 - 1, 1);					// 10月1日にセット
		col = date.get(Calendar.DAY_OF_WEEK);		// 曜日計算 1:日，2:月，3:火，・・7:土
		count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// 月曜日の場合
				count++;
				if (count == 2) {					// 第2月曜日
					Holiday[11] = 10 * 100 + c;
					break;
				}
			}
			col++;									// 次の曜日にセット
			if (col > 7)							// 土曜日の次は
				col = 1;							// 日曜日
		}

		// 春分の日   03/??
		Holiday[3] = 3*100 + (int)(20.8431+0.242194*(Year-1980)-(int)((Year-1980)/4)); 
		// 秋分の日   09/??
		Holiday[10] = 9*100 + (int)(23.2488+0.242194*(Year-1980)-(int)((Year-1980)/4));
	}
	// ActionListenerインターフェースのメソッドを定義 -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// アクションイベント処理
		Button button = (Button)evt.getSource( );
		if (button == LastYearButton)				// ボタンのラベルが前年の場合
			Year--;
		if (button == NextYearButton)				// ボタンのラベルが翌年の場合
			Year++;
		if (button == LastMonthButton)				// ボタンのラベルが前月の場合
			Month--;
		if (button == NextMonthButton)				// ボタンのラベルが翌月の場合
			Month++;
		if (Month < 1) {							// 1月の前の月
			Year--;									// 前年の12月にする
			Month = 12;	
		}
		if (Month > 12) {							// 12月の次の月
			Year++;									// 翌年の1月にする
			Month = 1;
		}
		repaint( );									// 再描画
	}
}
