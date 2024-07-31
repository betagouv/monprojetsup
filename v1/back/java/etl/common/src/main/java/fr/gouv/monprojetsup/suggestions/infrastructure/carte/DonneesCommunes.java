package fr.gouv.monprojetsup.suggestions.infrastructure.carte;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;


/**
 * 
 * @author Bryan
 *	Classe contenant diverses données commune a l'application. 
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class DonneesCommunes {
	/** Singleton de la classe */
	protected static final DonneesCommunes singleton;

	private static final Logger LOGGER = Logger.getLogger(DonneesCommunes.class.getSimpleName());
    
    
    /* L'ensemble des formations (v_for)*/
    private final Map<Integer, Item> formations = new HashMap<>();
    
    
    /* L'ensemble des types de formations (v_typ_for) */
    private final Map<Integer, Item> typesFormation = new HashMap<>();
    
    
    /* L'ensemble des filieres (v_fil) */
    private final Map<Integer, Item> filieres = new HashMap<>();
 
    /* Les types d'internats */
    private final Map<Integer, Item> typesInternat = new HashMap<>();

    
    /* L'ensemble des correspondances g_ta_cod/g_tf_cod
     * (Cas particulier car une formation peut correspondre à plusieurs type 
     * 	on charge donc en memoire l'ensemble des correspondance pour limiter les accés bases )*/
    private final Map<Integer, ArrayList<Integer>> correspondancesTypesFormation = new HashMap<>();
    
    
    /* L'ensemble des correspondances g_ta_cod/g_fl_cod
     * (Cas particulier car une formation peut correspondre à plusieurs type 
     * 	on charge donc en memoire l'ensemble des correspondance pour limiter les accés bases )*/
    private final Map<Integer, ArrayList<Integer>> correspondancesFilieres = new HashMap<>();
    

    
    
    /**
     * Les similitude entre les filieres
     */
    private final Map<Integer, ArrayList<Object[]>> similitudeFilieres = new HashMap<>();
    
    
    
	static { 
		singleton = new DonneesCommunes(); 
	}
	
	public static DonneesCommunes getInstance()
	{
		return singleton;
	}
	

	public Map<Integer, Item> getFilieres() {
		return filieres;
	}


	public Map<Integer, Item> getTypesInternat() {
		return typesInternat;
	}

	/**
	 * Charge les type de formations de PSUP
	 * @param connection
	 * @throws SQLException
	 */
    public void chargerTypesFormations(Connection connection, boolean fullMode) throws SQLException {
    	LOGGER.info("Chargement des type de formations");
        Item item;
   		try (Statement stmt = connection.createStatement()) {

   			stmt.setFetchSize(1_000_000);

   			String sql = "select * from v_typ_for";
               LOGGER.info(sql);

               try (ResultSet rs = stmt.executeQuery(sql)) {
            	   while (rs.next()) {
            		   item = new Item(
            		   		fullMode,
							   "tf"+rs.getInt("g_tf_cod"),
							   rs.getString("g_tf_lib"),
							   rs.getString("g_tf_sig"),
							   rs.getString("g_tf_des"),
							   null ,
							   rs.getString("g_tf_mot_cle_mdr"),
							   null,
							   rs.getInt("g_tf_ord_tri")
					   );
            		   this.typesFormation.put(rs.getInt("g_tf_cod"), item);
               		}
               }
   		}
    }
    
    
    
    /**
     * Charge les correspondance formation/type de formation
     * @param connection
     * @throws SQLException
     */
    public void chargerCorrespondancesTypesFormation(Connection connection) throws SQLException {
    	LOGGER.info("Chargement des correspondances des types de formations");
    	ArrayList<Integer> gTfCods;
    	try (Statement stmt = connection.createStatement()) {
   			stmt.setFetchSize(1_000_000);
   			String sql = "select * from sp_g_tri_aff_typ_for";
               LOGGER.info(sql);

           try (ResultSet rs = stmt.executeQuery(sql)) {
        	   while (rs.next()) {
        		   /* Si la formation n'est pas dans le tableau de correspondance on crée la correspondance. */
        		   if (!this.correspondancesTypesFormation.containsKey(rs.getInt("g_ta_cod"))) {
        			  gTfCods = new ArrayList<>();
        			  gTfCods.add(rs.getInt("g_tf_cod"));
        			  this.correspondancesTypesFormation.put(rs.getInt("g_ta_cod"), gTfCods);
        		  }else {
        			  /* Si la formation est deja dans le tableau de correspondance, on ajoute le type de formation.*/
        			  this.correspondancesTypesFormation.get(rs.getInt("g_ta_cod")).add(rs.getInt("g_tf_cod"));
        		  }
           		}
           }
   		}
    }
    
    
    /**
     * Charge les similitudes de filiere. g_fl_cod / String[g_fl_cod, poids]
     * @param connection
     * @throws SQLException
     */
    public void chargerSimilitudeFiliere(Connection connection) throws SQLException {
		LOGGER.info("Chargement des similitudes");

		try (Statement stmt = connection.createStatement()) {
			stmt.setFetchSize(1_000_000);
			String sql = "select * from v_fil_sim order by g_fl_cod_ori,g_fs_sco";
			LOGGER.info(sql);

			try (ResultSet rs = stmt.executeQuery(sql)) {
				while (rs.next()) {
					Object[] sim = new Object[3];
					sim[0] = "fl" + rs.getString("g_fl_cod_sim");
					sim[1] = rs.getInt("g_fs_sco");
					sim[2] = rs.getInt("i_tc_cod");

					/* Si la formation n'est pas dans le tableau de correspondance on crée la correspondance. */
					int gFlCod = rs.getInt("g_fl_cod_ori");
					this.similitudeFilieres.computeIfAbsent(gFlCod,x -> new ArrayList<>()).add(sim);
				}
			}
		}
	}
    	
    
    
    
    /**
     * 
     * @param gTaCod
     * @return une map codeItem/itemFormation pour le g_ta_cod passé en paramètre.
	 */
    public Map<String, Item> getTypesFormation(int gTaCod) {
    	
    	Map<String, Item> typesFor = new HashMap<>();
    	/* On parcoure les correspondances */
		this.correspondancesTypesFormation.forEach((k,v) -> {
			/* Si le g_ta_cod correspond*/
			if (k == gTaCod) {
				/* On parcoure le tableau des type de formation v et on ajoute l'item à la map de retour */
				for (Integer g_tf_cod : v) {
					/* Le code Item*/
					String gtf = "tf"+g_tf_cod;
					/* L'item lui meme récupéré dans la map typesFormation chargé en mémoire*/
					typesFor.put(gtf, this.typesFormation.get(g_tf_cod));
				}				
			}
		});		
  
    	return typesFor;
    }

    
    /**
     * 
     * @param gflCod
     * @return Une liste de code item de filiere similaire au g_fl_cod passé en parametre 
     */
	public List<Object[]> getFilieresSimilaire(int gflCod){
		ArrayList<Object[]> similaires = similitudeFilieres.get(gflCod);
		if(similaires == null || similaires.isEmpty())
			return Collections.emptyList();
		return new ArrayList<>(similaires);
	}
	
	
	
	
	/**
	 * Charge les filieres/specialité de PSUP
	 * @param connection
	 * @throws SQLException
	 */
    public void chargerFilieres(Connection connection, boolean fullMode) throws SQLException {
    	LOGGER.info("Chargement des filieres/spécialités");
        Item item;
   		try (Statement stmt = connection.createStatement()) {

   			stmt.setFetchSize(1_000_000);

   			String sql = "select * from v_fil_car";
               LOGGER.info(sql);

               try (ResultSet rs = stmt.executeQuery(sql)) {
            	   while (rs.next()) {
            		   item = new Item(
            		   		fullMode,
							   "fl"+rs.getInt("g_fl_cod"),
							   rs.getString("g_fl_lib_mdr"),
							   rs.getString("g_fl_sig"),
							   null,
							   null ,
							   rs.getString("g_fl_mot_cle_mdr"),
							   getFilieresSimilaire(rs.getInt("g_fl_cod")),
							   rs.getInt("g_tf_ord_tri"));
            		   this.filieres.put(rs.getInt("g_fl_cod"), item);
               		}
               }
   		}
    }
    
    
    
    
    /**
     * Charge les correspondance formation/filiere 
     * @param connection
     * @throws SQLException
     */
    public void chargerCorrespondancesFiliere(Connection connection) throws SQLException {
    	LOGGER.info("Chargement des correspondances de filieres");
    	ArrayList<Integer> gFlCods;
    	try (Statement stmt = connection.createStatement()) {
   			stmt.setFetchSize(1_000_000);
   			String sql = "select * from v_men_for_l1";
               LOGGER.info(sql);

           try (ResultSet rs = stmt.executeQuery(sql)) {
        	   while (rs.next()) {
        		   /* Si la formation n'est pas dans le tableau de correspondance on crée la correspondance. */
        		   if (!this.correspondancesFilieres.containsKey(rs.getInt("g_ta_cod"))) {
        			   gFlCods = new ArrayList<>();
        			   gFlCods.add(rs.getInt("g_fl_cod"));
        			  this.correspondancesFilieres.put(rs.getInt("g_ta_cod"), gFlCods);
        		  }else {
        			  /* Si la formation est deja dans le tableau de correspondance, on ajoute la filiere.*/
        			  this.correspondancesFilieres.get(rs.getInt("g_ta_cod")).add(rs.getInt("g_fl_cod"));
        		  }
           		}
           }
   		}
    }
    
    
    
    /**
     * 
     * @param gTaCod
     * @return une map codeItem/itemFiliere pour le gTaCod passé en paramètre.
	 */
    public Map<String, Item> getFilieres(int gTaCod) {
    	
    	Map<String, Item> filFor = new HashMap<>();
    	/* On parcoure les correspondances */
		this.correspondancesFilieres.forEach((k,v) -> {
			/* Si le gTaCod correspond*/
			if (k == gTaCod) {
				/* On parcoure le tableau des filiere v et on ajoute l'item à la map de retour */
				for (Integer g_fl_cod : v) {
					/* Le code Item*/
					String gfl = "fl"+g_fl_cod;
					/* L'item lui meme récupéré dans la map filieres chargé en mémoire*/
					Item item = this.filieres.get(g_fl_cod);
					if (item != null) {
						filFor.put(gfl, item);
					}else {
						LOGGER.warning("Aucune filiere chargé pour le code = "+g_fl_cod);
					}		
				}				
			}
		});		
    	return filFor;
    }
    
    
    
    
	/**
	 * Charge les formation de PSUP (g_for)
	 * @param connection
	 * @throws SQLException
	 */
    public void chargerFormations(Connection connection, boolean fullMode) throws SQLException {
    	LOGGER.info("Chargement des formations");
        Item item;
   		try (Statement stmt = connection.createStatement()) {

   			stmt.setFetchSize(1_000_000);

   			String sql = "select * from v_for";
               LOGGER.info(sql);

               try (ResultSet rs = stmt.executeQuery(sql)) {
            	   while (rs.next()) {
            		   item = new Item(fullMode,"fr"+rs.getInt("g_fr_cod"), rs.getString("g_fr_lib"), rs.getString("g_fr_sig"), null, null , null, null, rs.getInt("g_fr_ord_tri"));
            		   this.formations.put(rs.getInt("g_fr_cod"), item);
               		}
               }
   		}
    }
    
    
    public void chargerTypesInternat(boolean fullMode) {
    	 this.typesInternat.put(0,
 			   	new Item(fullMode,"int0", "Etablissements sans internat", "Sans internat", null, null , null, null, 4)
 			   );
    	 
    	 this.typesInternat.put(1,
			   	new Item(fullMode, "int1", "Etablissements avec internat pour filles", "Internat filles", null, null , null, null, 2)
			   );

    	 this.typesInternat.put(2,
			   	new Item(fullMode, "int2", "Etablissements avec internat pour garçons", "Internat garçons", null, null , null, null, 3)
			   );
    	 this.typesInternat.put(3,
			   	new Item(fullMode, "int3", "Etablissements avec internat pour filles et garçons", "Internat mixte", null, null , null, null, 1)
			   );
    	 this.typesInternat.put(4,
			   	new Item(fullMode, "int4", "Etablissements avec internat non obligatoire", "Internat non obl.", null, null , null, null, 5)
			   );
    	
    }

	public static String cleanStr(String data) {

      	if(data == null) {
    		return null;
    	}
    	data = data.toLowerCase();
        data = data.replace("ô", "o");
        data = data.replace("à", "a");
        data = data.replace("â", "a");
        data = data.replace("é", "e");
        data = data.replace("è", "e");
        data = data.replace("ê", "e");
        data = data.replace("ë", "e");
        data = data.replace("É", "e");
        data = data.replace(".", "");
        data = data.replace("ï", "i");
        data = data.replace("î", "i");
		data = data.toLowerCase();


		String[] toRemove = {
                          "#or", "#", "\"", ":", "&", "(", ")", 
                          "[" , "]", "+", "-", "–", ",", "/", 
                          ";", " a ", " le ", " la ", " les ", " aux ",
                          " du ", " des ", " de ", " dans ", 
                          " et ", " en ", " avec ", " l'", " d'", " c'", "\n" 
         };

        Set<String> wordset = new HashSet<>();
		Collections.addAll(wordset,"le","la","les","aux","du","de","des","dans","et","en","avec","rue","avenue","boulevard");

		for (String s : toRemove) {
			data = data.replace(s, " ");
		}

        data = data.replace("  ", " ");

        String[] tokens = data.split("\\s+");

		StringBuilder dataBuilder = new StringBuilder();
		for (String token  : tokens) {
            if (!wordset.contains(token)) {
				wordset.add(token);
                dataBuilder.append(token).append(" ");
            }
        }
		data = dataBuilder.toString();
		return data;

    }
}
