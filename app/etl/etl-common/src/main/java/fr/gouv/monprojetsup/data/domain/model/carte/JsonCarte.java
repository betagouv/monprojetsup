package fr.gouv.monprojetsup.data.domain.model.carte;

import java.util.Map;
import java.util.TreeMap;


public class JsonCarte {
	
	/* L'index de chaque groupe dans le tableau groupes */
	public static final int IDX_TYPE_BAC = 0;
	public static final int IDX_TYPE_ETAB = 1;
	public static final int IDX_APPRENTISSAGE = 2;
	public static final int IDX_TYPE_FORMATION = 3;
	public static final int IDX_MENTION = 4;
	public static final int IDX_INTERNAT= 5;
    public static final int IDX_CRITERES_SPEC= 6;
    // Toujours placer IDX_AUTRE en dernier car filtre invisible sinon créaion de décalage
    public static final int IDX_AUTRE= 7;
    

	/* Nombre de groupe implémenté */
	public static final int NB_GRP = 8;

	/* Les valeur de g_par*/
	public final Map<Integer,String> parametres = new TreeMap<>();
	/* La liste des groupes*/
	public final Groupe[] groupes = new Groupe[NB_GRP];
	/* Map g_ea_cod / Etablissement */
	public final Map<String,Etablissement> etablissements = new TreeMap<>();
	/* Map g_ta_cod / Formation */
	public final Map<Integer, FormationCarte> formations = new TreeMap<>();
	/* Map g_fl_cod / Filiere */
	public final Map<Integer, FiliereCarteTags> filieres = new TreeMap<>();

	public Lexique lexique;

	/* Constructeur par defaut */
	public JsonCarte() {
		super();
		/* Les groupes de filtre que l'on souhaite */
		this.groupes[IDX_TYPE_ETAB] = new Groupe("tc","Types d'établissement", "Type etab.", "false",null, null);
		this.groupes[IDX_APPRENTISSAGE] = new Groupe("app", "Apprentissage", "Apprentissage", "false",null, null);
		this.groupes[IDX_TYPE_FORMATION] = new Groupe("tf", "Types de formation", "Type form.", "false",null, null);
		this.groupes[IDX_MENTION] = new Groupe("fl", "Mentions/Spécialités", "Ment./Spé.", "false",5, "tf");
		this.groupes[IDX_INTERNAT] = new Groupe("int","Internat", "Int.", "true",null, null);
		this.groupes[IDX_CRITERES_SPEC] = new Groupe("amg","Aménagement", "Amg", "true",null, null);
		this.groupes[IDX_AUTRE] = new Groupe("aut","autre", "Aut", "true",null, null);
		this.groupes[IDX_TYPE_BAC] = new Groupe("tb","Type de Bac", "Type bac", "true", null, null);
		/* Ce groupe ne sera pas visible sur la carte*/
		this.groupes[IDX_AUTRE].setAff("false");
	}
	


}
