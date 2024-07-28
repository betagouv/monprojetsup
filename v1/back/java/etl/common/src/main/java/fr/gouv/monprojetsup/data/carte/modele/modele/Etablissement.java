package fr.gouv.monprojetsup.data.carte.modele.modele;

import java.sql.ResultSet;
import java.sql.SQLException;


@SuppressWarnings("unused")
public class Etablissement {
	/* Nom */
	private String nm;
	/* Nom court */
	private String nmc;
	/* Position lat/lng */
	private Float[] pos;
	/* Url etab*/
	private String url;

	public Etablissement(String nm, String nmc, Float[] pos, String url) {
		super();
		this.nm = nm;
		this.nmc = nmc;
		this.pos = pos;
		this.url = url;
	}
	
	public Etablissement(ResultSet rs) throws SQLException {
		Float[] etaPos = new Float[2];
		etaPos[0] = rs.getFloat("g_ea_lat");
		etaPos[1] = rs.getFloat("g_ea_lng");
	
		this.nm = rs.getString("g_ea_lib_mdr");
		this.nmc = rs.getString("g_ea_lib_cou_mdr");
		this.pos = etaPos;
		this.url = rs.getString("g_ea_sit_int");
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
	public Float[] getPos() {
		return pos;
	}
	public void setPos(Float[] pos) {
		this.pos = pos;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
	
}
