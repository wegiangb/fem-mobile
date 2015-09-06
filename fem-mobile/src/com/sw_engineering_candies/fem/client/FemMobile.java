/**
 * Copyright (C) 2012-2015, Markus Sprunck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - The name of its contributor may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sw_engineering_candies.fem.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FemMobile implements EntryPoint {

	final static boolean isModel2Active = false;

	final static Solver model = new Solver();

	static Double beta = 0.0;

	static Double gamma = 0.0;

	static String selecedElementId = "";

	static boolean isGravityActive = false;

	public FemMobile() {
		final String inputModel2 = ModelFactory.createDefaultModel(400, 40, 40, 4, 1).toString();
		// final String inputModel2 = ModelFactory.createEiffelTowerModel();

		model.createModel(inputModel2);
		setModel(model.getJSON());
	}

	public void updateModel() {
		
		getValuesFromGui();

		final double start = System.currentTimeMillis();
		final Vector forces = model.caluculateInputForces(beta, gamma, isGravityActive, selecedElementId);
		model.solve(forces);
		setModel(model.getJSON());

		final double end = System.currentTimeMillis();
		System.out.println("update model ready       [" + (end - start) + "ms]");
	}

	public static void getValuesFromGui() {

		final String currentBetaNew = getBeta();
		if (null != currentBetaNew && !currentBetaNew.isEmpty()) {
			beta = Double.valueOf(currentBetaNew);
		}

		final String currentGammaNew = getGamma();
		if (null != currentGammaNew && !currentGammaNew.isEmpty()) {
			gamma = Double.valueOf(currentGammaNew);
		}

		final String currentIsGravityActive = isGravityActive();
		if (null != currentIsGravityActive && !currentIsGravityActive.isEmpty()) {
			isGravityActive = Boolean.parseBoolean(currentIsGravityActive);
		}

		if (null != currentIsGravityActive && !currentIsGravityActive.isEmpty()) {
			selecedElementId = selecedElementId();
		}

	}

	public static native void exportStaticMethod() /*-{
		$wnd.updateForces = $entry(@com.sw_engineering_candies.fem.client.FemMobile::getValuesFromGui());
	}-*/;

	public static native void setModel(String model)
	/*-{
		$wnd.setModel(model);
	}-*/;

	public static native String getBeta()
	/*-{
		return $wnd.getBeta();
	}-*/;

	public static native String getGamma()
	/*-{
		return $wnd.getGamma();
	}-*/;

	public static native String isGravityActive()
	/*-{
		return $wnd.isGravityActive();
	}-*/;

	public static native String selecedElementId()
	/*-{
		return $wnd.selecedElementId();
	}-*/;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		exportStaticMethod();

		final Timer timerGraficUpdate = new Timer() {
			@Override
			public void run() {
				updateModel();
			}
		};
		timerGraficUpdate.scheduleRepeating(250);
	}

}
