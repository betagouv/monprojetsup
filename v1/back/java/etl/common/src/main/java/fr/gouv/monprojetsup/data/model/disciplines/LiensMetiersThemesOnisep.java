package fr.gouv.monprojetsup.data.model.disciplines;


import java.util.List;

public record LiensMetiersThemesOnisep(InnerContent contents) {

    public record InnerContent(List<Content> content) {

    }
    public record Content(
            String title,
            UniqueId uniqueID,
            List<ThesaurusIdeoDisciplines> thesaurusIdeoDisciplines
    ) {
        public String keyMetier() {
            return uniqueID().text;
        }
    }

    public record UniqueId(String text) {

    }

    public record ThesaurusIdeoDisciplines(
            UniqueId uniqueID
    ) {
        public String keyTheme() {
            return uniqueID().text;
        }

    }
}
