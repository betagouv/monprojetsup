package fr.gouv.monprojetsup.data.infrastructure.carte;

import java.util.List;

/**
 * 
 * @author Bryan
 * 
 */
@SuppressWarnings("unused")
public class Item {
    
	/* ID */
	private String id;
	/* Nom */
	private String nm;
	/* Nom court */
	private String nmc;
	/* description */
	private String dsc;
	/* url */
	private String url;

	/* Mots clés pour la recherche */
	private String rec;
	
	private List<Object[]> sim;
	
	/* Ordre de tri des items */
	private Integer ord;

	
	
	public Item(boolean full,String id, String nm, String nmc, String dsc, String url, String rec, List<Object[]> sim, Integer ord) {
		super();
		this.id = id;
		this.nm = nm;
		this.nmc = nmc;
		this.dsc = dsc;
		this.url = url;
		if (full) {
			this.rec = DonneesCommunes.cleanStr(rec);
		}else {
			this.rec = null;
		}
		this.sim = sim;
		this.ord = ord;
	}
	
	public Item(String id, String nm, String nmc, String dsc, String url, String rec, Integer ord) {
		super();
		this.id = id;
		this.nm = nm;
		this.nmc = nmc;
		this.dsc = dsc;
		this.url = url;
		this.rec = DonneesCommunes.cleanStr(rec);
		this.sim = null;
		this.ord = ord;
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOrd(Integer ord) {
		this.ord = ord;
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
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<Object[]> getSim() {
		return sim;
	}
	public void setSim(List<Object[]> sim) {
		this.sim = sim;
	}
	public String getRec() {
		return rec;
	}
	public void setRec(String rec) {
		this.rec = rec;
	}
	public int getOrd() {
		if (this.ord == null) {
			return 0;
		}else {
			return ord;
		}
	}
	public void setOrd(int ord) {
		this.ord = ord;
	}
	
	
	
	
}


