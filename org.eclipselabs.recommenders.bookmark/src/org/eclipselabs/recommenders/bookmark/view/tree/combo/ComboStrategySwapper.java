package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import java.util.LinkedList;

import org.eclipselabs.recommenders.bookmark.model.Category;

public class ComboStrategySwapper {
    
    private LinkedList<IChangeableStrategy> strategies = new LinkedList<IChangeableStrategy>();
    
    public void register(IChangeableStrategy strategy){
        strategies.add(strategy);
    }
    
    
    public void resetStrategies() {
        for (IChangeableStrategy strategy : strategies){
            strategy.reset();
        }
    }
    
    public void setComboViewerSpecificStrategies(Category category){
        for (IChangeableStrategy strategy : strategies){
            strategy.change(category);
        }
    }
    
    public void update(Category category){
        for (IChangeableStrategy strategy : strategies){
            strategy.update(category);
        }
    }

}
