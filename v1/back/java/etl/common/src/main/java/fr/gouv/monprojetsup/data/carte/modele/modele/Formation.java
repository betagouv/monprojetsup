package fr.gouv.monprojetsup.data.carte.modele.modele;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "FieldCanBeLocal", "unused"})
@Data
public class Formation {
	/* Nom */
	private String nm;
	/* Nom court */
	private String nmc;
	/* g_ea_cod etab */
	private String gea;
	/* g_ea_lib  */
	private String gealib;
	/* g_ti_cod Formation ins */
	private int gti;
	/* g_ta_cod_ref pour les formations avec plusieurs point gps */
	private final Integer gtaref;
	/*code des groupes de la formation (filtre) */
	private List<String> grp;
	/* place de la formation*/
	private Integer pla;
	/* places libres par type bacs seulement en PC */
	private int[] dis;
	/* Taux d'accès pour chaque bac n-1 */
	private int[] acc;
	/* entier pour ordre d'affichage (généré avec algo)*/
	private final int rnd;
	/* position gps */
	private Float[] pos;
	/* filiere */
	private final int fl;
	/* Mots clés pour la recherche, index -> pertinence */
	private final Map<Integer,Integer> recW = new HashMap<>();
	/* Phrases pour la recherche, avec leur pertinence */
	private final Map<String,Integer> recS = new TreeMap<>(Comparator.naturalOrder());

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public Formation(
			ResultSet rs,
			boolean full,
			boolean isPc) throws SQLException {

    	dis = new int[4];
    	dis[0] = rs.getInt("sc_flc_grp_bac_gen");
		dis[1] = rs.getInt("sc_flc_grp_bac_tec");
		dis[2] = rs.getInt("sc_flc_grp_bac_pro");
		dis[3] = rs.getInt("sc_flc_grp_aut");
		if(dis[0] + dis[1] + dis[2] + dis[3] == 0) {
			dis = null;
		}

		pos = new Float[2];
		if (rs.getFloat("g_ta_lat") == rs.getFloat("g_ea_lat") && rs.getFloat("g_ta_lng") == rs.getFloat("g_ea_lng")) {
			pos = null; //coordonnée formation = coordonnée etab donc on n'ecrit pas dans le json
		}else {
			pos[0] = rs.getFloat("g_ta_lat");
			pos[1] = rs.getFloat("g_ta_lng");
		}

		this.nm = rs.getString("g_ta_lib_mdr");
		this.nmc = rs.getString("g_ta_lib_cou_mdr");
		this.gea = rs.getString("g_ea_cod_aff");
		this.gealib = null;
		this.gti = rs.getInt("g_ti_cod");
		this.grp = new ArrayList<>();
		this.pla = rs.getString("a_rc_cap_inf") != null ? rs.getInt("a_rc_cap_inf") : -1;
		this.rnd = rs.getInt("g_ta_poi_mdr");
		this.fl = rs.getInt("g_fl_cod_aff");

		if (rs.getString("g_ta_cod_ref") != null) {
			this.gtaref = rs.getInt("g_ta_cod_ref");
		}else {
			this.gtaref = null;
		}
		
	}

	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getNmc() {
		return nmc;
	}
	public void setNmc(String nmc) {
		this.nmc = nmc;
	}
	public String getGea() {
		return gea;
	}
	public void setGea(String gea) {
		this.gea = gea;
	}
	public int getGti() {
		return gti;
	}
	public void setGti(int gti) {
		this.gti = gti;
	}
	public List<String> getGrp() {
		return grp;
	}
	public void setGrp(List<String> grp) {
		this.grp = grp;
	}
	public int getPla() {
		return pla;
	}
	public void setPla(int pla) {
		this.pla = pla;
	}
	public int[] getDis() {
		return dis;
	}
	public void setDis(int[] dis) {
		this.dis = dis;
	}
	public int[] getAcc() {
		return acc;
	}
	public void setAcc(int[] acc) {
		this.acc = acc;
	}
	public Float[] getPos() {
		return pos;
	}
	public void setPos(Float[] pos) {
		this.pos = pos;
	}

	public String getGealib() {
		return gealib;
	}

	public void setGealib(String gealib) {
		this.gealib = gealib;
	}


}
