package it.uniroma1.lcl.crucyservlet.mongodb;

public enum Difficulty
{
    EASY(0.0, 0.3),
    MEDIUM(0.3, 0.5),
    HARD(0.5, 1.0);

    public final Double lower;
    public final Double upper;

    private Difficulty(Double lower, Double upper)
    {
        this.lower=lower;
        this.upper=upper;
    }

    public static Difficulty valueOfDifficulty(Double difficulty){
        for (Difficulty diff : values())
            if (difficulty >= diff.lower && difficulty < diff.upper)
                return diff;
        return null;
    }

    public static void main(String[] args){

        System.out.println(Difficulty.valueOf("EASY").upper);
        System.out.println(Difficulty.valueOfDifficulty(1.0));
    }
}

