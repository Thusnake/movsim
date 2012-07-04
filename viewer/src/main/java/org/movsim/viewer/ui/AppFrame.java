/*
 * Copyright (C) 2010, 2011, 2012 by Arne Kesting, Martin Treiber, Ralph Germ, Martin Budden
 * <movsim.org@gmail.com>
 * -----------------------------------------------------------------------------------------
 * 
 * This file is part of
 * 
 * MovSim - the multi-model open-source vehicular-traffic simulator.
 * 
 * MovSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MovSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MovSim. If not, see <http://www.gnu.org/licenses/>
 * or <http://www.movsim.org>.
 * 
 * -----------------------------------------------------------------------------------------
 */
package org.movsim.viewer.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.movsim.input.ProjectMetaData;
import org.movsim.simulator.Simulator;
import org.movsim.viewer.graphics.TrafficCanvas;
import org.movsim.viewer.util.SwingHelper;

@SuppressWarnings("synthetic-access")
public class AppFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final CanvasPanel canvasPanel;
    final StatusPanel statusPanel;
    private MovSimToolBar toolBar;

    public AppFrame(ResourceBundle resourceBundle, ProjectMetaData projectMetaData) {
        super(resourceBundle.getString("FrameName"));

        SwingHelper.activateWindowClosingAndSystemExitButton(this);

        final Simulator simulator = new Simulator(projectMetaData);
        initLookAndFeel();

        final TrafficCanvas trafficCanvas = new TrafficCanvas(simulator);
        canvasPanel = new CanvasPanel(resourceBundle, trafficCanvas);
        statusPanel = new StatusPanel(resourceBundle, simulator);

        addToolBar(resourceBundle, trafficCanvas);
        addMenu(resourceBundle, simulator, trafficCanvas);

        add(canvasPanel, BorderLayout.CENTER);
        add(toolBar, BorderLayout.NORTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvasPanel.resized();
                canvasPanel.repaint();
            }
        });

//        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        setSize(1200, 600);

        // first scenario
        String projectName = projectMetaData.getProjectName();
        if (projectName.equals("")) {
            projectName = "ramp_metering";
            trafficCanvas.setupTrafficScenario(projectName, "../sim/games/");
        } else {
            trafficCanvas.setupTrafficScenario(projectName, projectMetaData.getPathToProjectXmlFile());
        }
        if (projectName.equals("routing") || projectName.equals("ramp_metering")) {
            trafficCanvas.setVehicleColorMode(TrafficCanvas.VehicleColorMode.EXIT_COLOR);
        }
        statusPanel.reset();
        trafficCanvas.start();
        setVisible(true);
    }

    /**
     * @param resourceBundle
     */
    private void addToolBar(ResourceBundle resourceBundle, TrafficCanvas trafficCanvas) {
        toolBar = new MovSimToolBar(statusPanel, trafficCanvas, resourceBundle);
    }

    /**
     * @param resourceBundle
     */
    private void addMenu(ResourceBundle resourceBundle, Simulator simulator, TrafficCanvas trafficCanvas) {
        final AppMenu trafficMenus = new AppMenu(this, simulator, canvasPanel, trafficCanvas, resourceBundle);
        trafficMenus.initMenus();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("set to system LaF");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }
}
