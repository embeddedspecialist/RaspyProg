/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import sceneManager.*;
import java.awt.Font;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author amirrix
 */
public class AFONodeWidget extends Widget {
    
    protected Widget overallWidget;
    protected Widget moduleWidget;
    protected Widget moduleINPinNames;
    protected Widget moduleOUTPinNames;
    protected Widget inputs;
    protected Widget outputs;
    protected LabelWidget moduleComment;
    
    private Widget moduleNameWidget;
    private LabelWidget moduleTypeWidget;
    private LabelWidget moduleSubTypeWidget;
    private AFOGraphPinScene scene;
    
    boolean isRotated;

    public AFONodeWidget(AFOGraphPinScene scene) {
        super(scene);
        
        this.scene = scene;
        
        isRotated = false;
        
        setLayout (LayoutFactory.createVerticalFlowLayout ());
        setBorder (CommonDefinitions.BORDER_4);
        setOpaque (true);
        setCheckClipping (true);
        
        overallWidget = new Widget(scene);
        overallWidget.setLayout (LayoutFactory.createHorizontalFlowLayout ());
        overallWidget.setBorder (CommonDefinitions.BORDER_EMPTY);
        overallWidget.setOpaque (true);
        overallWidget.setCheckClipping (true);
        this.addChild(overallWidget);
        
        moduleComment = new LabelWidget (scene,"comment");
        moduleComment.setAlignment(LabelWidget.Alignment.CENTER);
        moduleComment.setFont (new Font(Font.SANS_SERIF,Font.PLAIN, 10));
        moduleComment.setUseGlyphVector (true);
        this.addChild(moduleComment);

        inputs = new Widget (scene);
        inputs.setLayout (LayoutFactory.createVerticalFlowLayout ());
        inputs.setOpaque (false);
        inputs.setBorder (CommonDefinitions.BORDER_4);
        overallWidget.addChild (inputs);
        
        moduleWidget = new Widget (scene);
        moduleWidget.setLayout (LayoutFactory.createHorizontalFlowLayout ());
        moduleWidget.setBorder (CommonDefinitions.BORDER_LINE);
        overallWidget.addChild (moduleWidget);
        
        moduleINPinNames = new Widget(scene);
        moduleINPinNames.setLayout (LayoutFactory.createVerticalFlowLayout());
        moduleINPinNames.setBorder (CommonDefinitions.BORDER_4);
        moduleWidget.addChild (moduleINPinNames);
        
        moduleNameWidget = new Widget (scene);
        moduleNameWidget.setLayout(LayoutFactory.createVerticalFlowLayout());
        moduleNameWidget.setBorder(CommonDefinitions.BORDER_4);
        moduleWidget.addChild (moduleNameWidget);
        
        moduleTypeWidget = new LabelWidget (scene);
        moduleTypeWidget.setFont (scene.getDefaultFont ().deriveFont (Font.BOLD));
        moduleTypeWidget.setFont (moduleTypeWidget.getFont().deriveFont (10.0f));
        moduleTypeWidget.setAlignment(LabelWidget.Alignment.CENTER);
        moduleTypeWidget.setVerticalAlignment(LabelWidget.VerticalAlignment.CENTER);
        moduleTypeWidget.setBorder (CommonDefinitions.BORDER_4);
        moduleTypeWidget.setUseGlyphVector (true);
        moduleNameWidget.addChild (moduleTypeWidget);
        
        moduleSubTypeWidget = new LabelWidget (scene);
        moduleSubTypeWidget.setFont (scene.getDefaultFont ().deriveFont (Font.PLAIN));
        moduleSubTypeWidget.setFont (moduleTypeWidget.getFont().deriveFont (8.0f));
        moduleSubTypeWidget.setAlignment(LabelWidget.Alignment.CENTER);
        moduleSubTypeWidget.setVerticalAlignment(LabelWidget.VerticalAlignment.CENTER);
        moduleSubTypeWidget.setBorder (CommonDefinitions.BORDER_4);
        moduleSubTypeWidget.setUseGlyphVector (true);
        moduleNameWidget.addChild (moduleSubTypeWidget);

        moduleOUTPinNames = new Widget(scene);
        moduleOUTPinNames.setLayout (LayoutFactory.createVerticalFlowLayout());
        moduleOUTPinNames.setBorder (CommonDefinitions.BORDER_4);
        moduleWidget.addChild (moduleOUTPinNames);

        outputs = new Widget (scene);
        outputs.setLayout (LayoutFactory.createVerticalFlowLayout ());
        outputs.setOpaque (false);
        outputs.setBorder (CommonDefinitions.BORDER_4);
        overallWidget.addChild (outputs);
        
        getActions().addAction(scene.createWidgetHoverAction());
        getActions().addAction(scene.createSelectAction());
        
    }


    public void setName(String name) {
        this.moduleTypeWidget.setLabel(name);
    }
    
    public String getName() {
        return this.moduleTypeWidget.getLabel();
    }


    public void setSubType(String subType){
        this.moduleSubTypeWidget.setLabel(subType);
    }
    
    public String getSubType(){
        return this.moduleSubTypeWidget.getLabel();
    }

    public LabelWidget getModuleComment() {
        return moduleComment;
    }

    public void clearPins() {
        inputs.removeChildren();
        outputs.removeChildren();
    }

    public AFOPinWidget addPin(AFOPin pin) {
        AFOPinWidget member = new AFOPinWidget(scene);
        LabelWidget pinNameWidget;
        
        if (pin.getPinType() == AFOPin.E_PinType.PIN_INPUT)
        {
            member.setLabel("  >");
            member.setInputState(true);
            inputs.addChild(member);
            pinNameWidget = new LabelWidget(scene, pin.getLabel());
            moduleINPinNames.addChild(pinNameWidget);
            pin.setPinNameWidget(pinNameWidget);
        }
        else if (pin.getPinType() == AFOPin.E_PinType.PIN_COMMAND)
        {
            member.setLabel("  *");
            member.setInputState(true);
            inputs.addChild(member);
            pinNameWidget = new LabelWidget(scene, pin.getLabel());
            moduleINPinNames.addChild(pinNameWidget);
            pin.setPinNameWidget(pinNameWidget);
        }
        else
        {
            member.setLabel(">  ");
            outputs.addChild(member);
            pinNameWidget = new LabelWidget(scene,pin.getLabel());
            moduleOUTPinNames.addChild(pinNameWidget);
            pin.setPinNameWidget(pinNameWidget);
        }

        //Abilitando questo le scritte NON sono piu' allineate con i pin
//        pinNameWidget.setUseGlyphVector (true);
        return member;
    }
            
    public Widget getNameWidget() {
        return moduleNameWidget;
    }
    
    @Override
    protected void notifyStateChanged (ObjectState previousState, ObjectState state) {
        
        if (state.isSelected()) {
            moduleWidget.setBorder(CommonDefinitions.BORDER_FILL_DARKYELLOW);
            return;
        }
        else {
            if (state.isHovered()) {
                moduleWidget.setBorder(CommonDefinitions.BORDER_FILL_LIGHTYELLOW);
                return;
            }
            else {
                moduleWidget.setBorder(CommonDefinitions.BORDER_LINE);
            }
        }
    }
    
    public Widget GetRotatedInstance() {
        int i = 0;
        //Pulisco il widget
        overallWidget.removeChildren();
        moduleWidget.removeChildren();
        overallWidget.addChild (outputs);
        moduleWidget.addChild (moduleOUTPinNames);
        moduleWidget.addChild (moduleNameWidget);
        moduleWidget.addChild (moduleINPinNames);
        overallWidget.addChild (moduleWidget);
        overallWidget.addChild (inputs);
        
        //Giro il testimone
        for (i = 0; i < inputs.getChildren().size();i++) {
            ((LabelWidget)inputs.getChildren().get(i)).setLabel("<");
        }
        
        for (i = 0; i < outputs.getChildren().size();i++) {
            ((LabelWidget)outputs.getChildren().get(i)).setLabel("<");
        }
        
        isRotated = true;
        
        return this;
    }
    
    public Widget GetDirectInstance() {
        
        int i;
        
        //Pulisco il widget
        overallWidget.removeChildren();
        moduleWidget.removeChildren();
        
        //Lo ricostruisco
        overallWidget.addChild (inputs);
        overallWidget.addChild (moduleWidget);
        moduleWidget.addChild (moduleINPinNames);
        moduleWidget.addChild (moduleNameWidget);
        moduleWidget.addChild (moduleOUTPinNames);
        overallWidget.addChild (outputs);
        
        //Giro il testimone
        for (i = 0; i < inputs.getChildren().size();i++) {
            ((LabelWidget)inputs.getChildren().get(i)).setLabel(">");
        }
        
        for (i = 0; i < outputs.getChildren().size();i++) {
            ((LabelWidget)outputs.getChildren().get(i)).setLabel(">");
        }
        
        isRotated = false;
        return this;
    }
    
    public boolean IsRotated() {
        
        return isRotated;
    }
    
    public Widget GetModuleWidget() {
        return moduleWidget;
    }

    public Widget getInputs() {
        return inputs;
    }

    public Widget getModuleINPinNames() {
        return moduleINPinNames;
    }

    public Widget getModuleOUTPinNames() {
        return moduleOUTPinNames;
    }

    public Widget getOutputs() {
        return outputs;
    }
    
    

}
