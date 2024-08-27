package fr.gouv.monprojetsup.data.domain.model.carte;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author Bryan
 *	Modelise un groupe (filtre)
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Groupe {
	/* Identifiant */
	private String id;
	/* Nom */
	private String nm;
	/* Nom court */
	private String nmc;
	/* description */
	private Map<String, Item> items;	
	/*Etat de l'accordeon du fitlre. null= pas d'accordeon, true=accordeon ouvert, false=accordeon fermé */
	private final String collapsable;
	
	private Integer nbaff;
	
	/*Id du groupe filtre parent */
	private String parent;
	
	private String aff;
	
	public Groupe(String id, String nm, String nmc, String collapsable,Integer nbaff, String parent) {
		super();
		this.id = id;
		this.nm = nm;
		this.nmc = nmc;
		this.collapsable = collapsable;
		this.nbaff=nbaff;
		this.parent = parent;
		this.items = new HashMap<>();
	}
	
	public Groupe(String nm, String nmc) {
		super();
		this.nm = nm;
		this.nmc = nmc;
		this.collapsable = null;
		this.items = new HashMap<>();
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
	public Map<String,Item> getItems() {
		return items;
	}
	public void setItems(Map<String,Item> items) {
		this.items = items;
	}
	
	public void ajouterItem(String code, Item item) {
		items.computeIfAbsent(code, z -> item);
	}

	public String getAff() {
		return aff;
	}

	public void setAff(String aff) {
		this.aff = aff;
	}
	
	
	
	
}
