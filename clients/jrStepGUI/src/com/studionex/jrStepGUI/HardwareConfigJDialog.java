package com.studionex.jrStepGUI;

/*
 * Copyright 2010 Jean-Louis Paquelin
 * 
 * This file is part of jrStepGUI.
 * 
 * jrStepGUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * jrStepGUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jrStepGUI.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact info: jrstepgui@studionex.com
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import com.studionex.misc.ui.MySwingUtilities;
import com.studionex.misc.ui.RegexFormatter;
import com.studionex.rStep.input.CurrentMessageEvent;
import com.studionex.rStep.input.FeedRateMessageEvent;
import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.StepByInchMessageEvent;
import com.studionex.rStep.input.SteppingMessageEvent;

@SuppressWarnings("serial")
public class HardwareConfigJDialog extends JDialog implements EventTopicSubscriber<InputEvent> {
	private JFormattedTextField[] sbiJFormattedTextField;
	private JFormattedTextField[] mfrJFormattedTextField;
	private JFormattedTextField[] currentJFormattedTextField;
	
	private SteppingJComboBox steppingJComboBox;
	private JFormattedTextField pwmJFormattedTextField;
		
	private Application application;
	
	public HardwareConfigJDialog(Application application) {
		super(application, "Configure rStep", true);
			// this is a modal JDialog
		
		this.application = application;
		
		// listen to coordinates rStep outputs
		EventBus.subscribe(Pattern.compile("RStep Step by inch:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Feed rate:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Current:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Stepping:.*"), this);

		buildUI();

		this.setSize(320, 280);
		
		// move this to the screen center
		MySwingUtilities.displayCentered(this); 
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// recall the stored values
		application.send("M201");
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());
		
		sbiJFormattedTextField = buildLabelAndTextField3D(0, 0, "Step by inch", "\\d+");
		mfrJFormattedTextField = buildLabelAndTextField3D(3, 0, "Max feed rate", "\\d+");
		currentJFormattedTextField = buildLabelAndTextField3D(3, 4, "Current", "\\d+");
		
		this.add(new JLabel("Stepping"),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 4,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(MainJPanel.GAP, MainJPanel.GAP, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 5,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.1, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		
		steppingJComboBox = new SteppingJComboBox();
		steppingJComboBox.setToolTipText("Stepping");
		this.add(steppingJComboBox,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 5,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));

		this.add(new JLabel("PWM duty cycle"),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 6,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(MainJPanel.GAP, MainJPanel.GAP, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 7,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.1, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		pwmJFormattedTextField = buildLabelAndTextField(1, 7, " ", "PWM duty cycle", "\\d+");

		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 8,
						/* gridwidth */ 6, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JButton(new AbstractAction("Update") {
			public void actionPerformed(ActionEvent event) {
				updateHardwareConfig();
			}}),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 9,
						/* gridwidth */ 6, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.SOUTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
	}
	
	private JFormattedTextField[] buildLabelAndTextField3D(int gridx, int gridy, String label, String regex) {
		this.add(new JLabel(label),
				new GridBagConstraints(
						/* gridx */ gridx, /* gridy */ gridy,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(MainJPanel.GAP, MainJPanel.GAP, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ gridx, /* gridy */ gridy + 1,
						/* gridwidth */ 1, /* gridheight */ 3,
						/* weightx */ 0.1, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		JFormattedTextField[] jFormattedTextField = new JFormattedTextField[3];
		jFormattedTextField[0] = buildLabelAndTextField(gridx + 1, gridy + 1, "X: ", label + " on X axis", regex);
		jFormattedTextField[1] = buildLabelAndTextField(gridx + 1, gridy + 2, "Y: ", label + " on Y axis", regex);
		jFormattedTextField[2] = buildLabelAndTextField(gridx + 1, gridy + 3, "Z: ", label + " on Z axis", regex);
		
		return jFormattedTextField;
	}
	
	private JFormattedTextField buildLabelAndTextField(int gridx, int gridy, String label, String tooltip, String regex) {
		this.add(new JLabel(label),
				new GridBagConstraints(
						/* gridx */ gridx, /* gridy */ gridy,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		JFormattedTextField jFormattedTextField = new JFormattedTextField(new RegexFormatter(regex));
		jFormattedTextField.setColumns(10);
		jFormattedTextField.setHorizontalAlignment(JTextField.RIGHT);
		jFormattedTextField.setToolTipText(tooltip);
		this.add(jFormattedTextField,
				new GridBagConstraints(
						/* gridx */ gridx + 1, /* gridy */ gridy,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */ GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));

		return jFormattedTextField;
	}
	
	public void onEvent(String topic, InputEvent inputEvent) {
		if(inputEvent instanceof StepByInchMessageEvent) {
			StepByInchMessageEvent stepByInchMessageEvent = (StepByInchMessageEvent)inputEvent;
			sbiJFormattedTextField[0].setValue(new Integer(stepByInchMessageEvent.getX()));
			sbiJFormattedTextField[1].setValue(new Integer(stepByInchMessageEvent.getY()));
			sbiJFormattedTextField[2].setValue(new Integer(stepByInchMessageEvent.getZ()));
		} else if(inputEvent instanceof FeedRateMessageEvent) {
			FeedRateMessageEvent feedRateMessageEvent = (FeedRateMessageEvent)inputEvent;
			mfrJFormattedTextField[0].setValue(new Integer(feedRateMessageEvent.getX()));
			mfrJFormattedTextField[1].setValue(new Integer(feedRateMessageEvent.getY()));
			mfrJFormattedTextField[2].setValue(new Integer(feedRateMessageEvent.getZ()));
		} else if(inputEvent instanceof CurrentMessageEvent) {
			CurrentMessageEvent currentMessageEvent = (CurrentMessageEvent)inputEvent;
			currentJFormattedTextField[0].setValue(new Integer(currentMessageEvent.getX()));
			currentJFormattedTextField[1].setValue(new Integer(currentMessageEvent.getY()));
			currentJFormattedTextField[2].setValue(new Integer(currentMessageEvent.getZ()));
		} else if(inputEvent instanceof SteppingMessageEvent) {
			SteppingMessageEvent steppingMessageEvent = (SteppingMessageEvent)inputEvent;
			steppingJComboBox.setStepping(steppingMessageEvent.getStepping());
		}
		// TODO: receive a message with the PWM duty cycle
		pwmJFormattedTextField.setValue(new Integer(128));
		
	}
	
	private void updateHardwareConfig() {
		// TODO: change should be applied iff the corresponding value has changed
		application.send(
				"M101 X" + (Integer)sbiJFormattedTextField[0].getValue() +
					" Y" + (Integer)sbiJFormattedTextField[1].getValue() +
					" Z" + (Integer)sbiJFormattedTextField[2].getValue() +
				" M102 X" + (Integer)mfrJFormattedTextField[0].getValue() +
					" Y" + (Integer)mfrJFormattedTextField[1].getValue() +
					" Z" + (Integer)mfrJFormattedTextField[2].getValue() +
				" M100 X" + (Integer)currentJFormattedTextField[0].getValue() +
					" Y" + (Integer)currentJFormattedTextField[1].getValue() +
					" Z" + (Integer)currentJFormattedTextField[2].getValue() +
				" M103 S" + steppingJComboBox.getStepping() +
				" M105 S" + (Integer)pwmJFormattedTextField.getValue() +
				" M201");
	}
	
	private static class SteppingJComboBox extends JComboBox {
		public SteppingJComboBox() {
			super();
			
			setEditable(false);
			setToolTipText("micro stepping value");
			addItem("full step");
			addItem("half step");
			addItem("quarter of step ");
			addItem("1/16th of step");
		}
		
		public int getStepping() {
			int index = Math.min(3, Math.max(0, getSelectedIndex()));
			switch(index) {
			case 3: return 16;
			case 2: return 4;
			case 1: return 2;
			default: return 1;
			}
		}

		public void setStepping(int stepping) {
			stepping = Math.min(16, Math.max(1, stepping));
			if(stepping > 10)
				setSelectedIndex(3);
			else if(stepping > 3)
				setSelectedIndex(2);
			else if(stepping > 1)
				setSelectedIndex(1);
			else
				setSelectedIndex(0);
		}
	}
	
}