package com.wsovine.backcheck;

public class Team {
    private int id = 0;
    private String name = "Error";
    private String abbreviation = "ERR";
    private int image = R.drawable.hockey_sticks;

    //Constructors
    public Team(int id, String name, String abbreviation){
        setId(id);
        setName(name);
        setAbbreviation(abbreviation);
        setImage();
    }

    //SET METHODS
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAbbreviation(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public void setImage() {
        switch (id){
            case 24:
                image = R.drawable.anaheim_ducks_primary;
                break;
            case 53:
                image = R.drawable.arizona_coyotes_primary;
                break;
            case 6:
                image = R.drawable.boston_bruins_primary;
                break;
            case 7:
                image = R.drawable.buffalo_sabres_primary;
                break;
            case 20:
                image = R.drawable.calgary_flames_primary;
                break;
            case 12:
                image = R.drawable.carolina_hurricanes_primary;
                break;
            case 16:
                image = R.drawable.chicago_blackhawks_primary;
                break;
            case 21:
                image = R.drawable.colorado_avalanche_primary;
                break;
            case 29:
                image = R.drawable.columbus_blue_jacket_primary;
                break;
            case 25:
                image = R.drawable.dallas_stars_primary;
                break;
            case 17:
                image = R.drawable.detroit_red_wings_primary;
                break;
            case 22:
                image = R.drawable.edmonton_oilers_primary;
                break;
            case 13:
                image = R.drawable.florida_panthers_primary;
                break;
            case 26:
                image = R.drawable.los_angeles_kings_primary;
                break;
            case 30:
                image = R.drawable.minnesota_wild_primary;
                break;
            case 8:
                image = R.drawable.montreal_canadiens_primary;
                break;
            case 18:
                image = R.drawable.nashville_predators_primary;
                break;
            case 1:
                image = R.drawable.new_jersey_devils_primary;
                break;
            case 2:
                image = R.drawable.new_york_islanders_primary;
                break;
            case 3:
                image = R.drawable.new_york_rangers_primary;
                break;
            case 9:
                image = R.drawable.ottawa_senators;
                break;
            case 4:
                image = R.drawable.philadelphia_flyers_primary;
                break;
            case 5:
                image = R.drawable.pittsburgh_penguins_primary;
                break;
            case 28:
                image = R.drawable.san_jose_sharks_primary;
                break;
            case 19:
                image = R.drawable.st_louis_blues_primary;
                break;
            case 14:
                image = R.drawable.tampa_bay_lightning;
                break;
            case 10:
                image = R.drawable.toronto_maple_leafs_primary;
                break;
            case 23:
                image = R.drawable.vancouver_canucks_primary;
                break;
            case 54:
                image = R.drawable.vegas_golden_knights_primary;
                break;
            case 15:
                image = R.drawable.washington_capitals_primary;
                break;
            case 52:
                image = R.drawable.winnipeg_jets_primary;
                break;
        }
    }

    //GET METHODS
    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public int getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getName();
    }
}
