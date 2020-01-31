package it.uniroma1.lcl.crucy.gameplay.loading;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import it.uniroma1.lcl.crucy.Animation;
import it.uniroma1.lcl.crucy.MainCrucy;

/**
 * con framer puó diventare uno Screen
 */
public class LoadingStage extends Stage implements Animation
{

    private LoadingActor actor;

    public LoadingStage(ExtendViewport extendViewport, MainCrucy main)
    {
        super(extendViewport,main.getBatch());
        actor = new LoadingActor(main,this);
        this.addActor(actor);

    }



    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void animationUpdate()
    {
    }
}
