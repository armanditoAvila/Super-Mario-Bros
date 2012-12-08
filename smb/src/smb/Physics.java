package smb;

public final class Physics {
	/* Framerate Constants */
	public static double second = 1000;
	public static double tenFrames = second / 6;
	public static double keyPoll = second / 30;
	
	/* @2x Constants For An @2x World */
	public static double bl = 32;          // Block
	public static double px = 2;          // Pixel
	public static double spx = 0.125;        // Subpixel
	public static double sspx = 0.0078125;    // Subsubpixel
	public static double ssspx = 0.00048828125;  // Subsubsubpixel
	
	/* Mario Ground Physics Constants */
	public static double mg_min_vel_walk = 60 * spx + 180 * sspx;
	public static double mg_acceler_walk = 540 * sspx + 480 * ssspx;
	public static double mg_acceler_runn = 840 * sspx + 240 * ssspx;
	public static double mg_deceler_rele = 780 * sspx;
	public static double mg_deceler_skid = 60 * spx + 600 * sspx;
	
	public static double mg_max_vel_walk = 60 * px + 540 * spx;
	public static double mg_max_vel_entr = 60 * px + 60 * spx;
	public static double mg_max_vel_uwwk = 780 * spx;
	public static double mg_max_vel_runn = 120 * px + 540 * spx;
	public static double mg_skid_turnaro = 540 * spx;
	
	/* Luigi Ground Physics Constants */
	public static double lg_min_vel_walk = 780 * sspx;
	public static double lg_acceler_walk = 360 * sspx + 480 * ssspx;
	public static double lg_acceler_runn = 660 * sspx + 240 * ssspx;
	public static double lg_deceler_rele = 600 * sspx;
	public static double lg_deceler_skid = 60 * spx + 600 * sspx;
	
	public static double lg_max_vel_walk = 60 * px + 540 * spx;
	public static double lg_max_vel_entr = 60 * px + 60 * spx;
	public static double lg_max_vel_uwwk = 780 * spx;
	public static double lg_max_vel_runn = 120 * px + 540 * spx;
	public static double lg_skid_turnaro = 540 * spx;
	
	/* Mario Air Physics Constants */
	public static double ma_hf_lessthan = 540 * sspx + 480 * ssspx;
	public static double ma_hf_greatore = 840 * sspx + 240 * ssspx;
	
	public static double ma_hb_greatore = 840 * sspx + 240 * ssspx;
	public static double ma_hb_fastjump = 780 * sspx;
	public static double ma_hb_slowjump = 540 * sspx + 480 * ssspx;
	
	/* Luigi Air Physics Constants */
	public static double la_hf_lessthan = 360 * sspx + 480 * ssspx;
	public static double la_hf_greatore = 660 * sspx + 240 * ssspx;
	
	public static double la_hb_greatore = 660 * sspx + 240 * ssspx;
	public static double la_hb_fastjump = 600 * sspx;
	public static double la_hb_slowjump = 360 * sspx + 480 * ssspx;
	
	/* Max Airspeed For Both*/
	public static double ba_max_air_lt = 60 * px + 540 * spx;
	public static double ba_max_air_gt = 120 * px + 540 * spx;
	
	/* Fall Speed Capping */
	public static double bf_max_vel_fall = 240 * px + 480 * spx;
	public static double bf_fall_vel_wrap = 240 * px;
	
	/* Mario Jump Physics Constants */
	public static double mj_lt_init_vel = 290 * px;
	public static double mj_bt_init_vel = 295 * px;
	public static double mj_gt_init_vel = 300 * px;
	public static double mj_le_init_vel = 0;
	
	public static double mj_lt_hold_gra = 120 * spx;
	public static double mj_bt_hold_gra = 60 * spx + 840 * sspx;
	public static double mj_gt_hold_gra = 120 * spx + 480 * sspx;
	public static double mj_le_hold_gra = 120 * spx + 480 * sspx;
	
	public static double mj_lt_fall_gra = 420 * spx;
	public static double mj_bt_fall_gra = 360 * spx;
	public static double mj_gt_fall_gra = 540 * spx;
	public static double mj_le_fall_gra = 120 * spx + 480 * sspx;
	
	/* Luigi Jump Physics Constants */
	public static double lj_lt_init_vel = 290 * px;
	public static double lj_bt_init_vel = 295 * px;
	public static double lj_gt_init_vel = 300 * px;
	public static double lj_le_init_vel = 0;
	
	public static double lj_lt_hold_gra = 60 * spx + 480 * sspx;
	public static double lj_bt_hold_gra = 60 * spx + 480 * sspx;
	public static double lj_gt_hold_gra = 120 * spx + 120 * sspx;
	public static double lj_le_hold_gra = 0;
	
	public static double lj_lt_fall_gra = 240 * spx + 240 * sspx;
	public static double lj_bt_fall_gra = 180 * spx + 840 * sspx;
	public static double lj_gt_fall_gra = 300 * spx + 780 * sspx;
	public static double lj_le_fall_gra = 120 * spx + 480 * sspx;
	
	/* Swimming Physics Constants */
	public static double bs_nm_init_vel = 60 * px + 480 * spx;
	public static double bs_wz_init_vel = 60 * px;
	public static double bs_sz_init_vel = 60 * px + 480 * spx;
	
	public static double bs_nm_hold_gra = 780 * sspx;
	public static double bs_wz_hold_gra = 240 * sspx;
	public static double bs_sz_hold_gra = 60 * spx + 480 * sspx;
	
	public static double bs_nm_fall_gra = 600 * sspx;
	public static double bs_wz_fall_gra = 540 * sspx;
	public static double bs_sz_fall_gra = 60 * spx + 480 * sspx;
	
	/* Misc Constants */
	public static double flagpoll_vel = 120 * px;
	public static double enemy_stomp_vel = 240 * px;
	public static double lt_jump = 60 * px;
	public static double bt_jump = 120 * px + 240 * spx + 900 * sspx + 900 * ssspx;
	public static double gt_jump = 120 * px + 300 * spx;
	public static double le_jump = 0;
}