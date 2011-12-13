package org.eclipselabs.recommenders.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class ComboFakeTooltip {

	private Combo combo;
	private ComboListener comboListener;
	private Shell shell;

	public ComboFakeTooltip(Combo combo) {
		this.combo = combo;
		combo.setToolTipText("");

		setUp();
	}

	private void setUp() {

		comboListener = new ComboListener();

		combo.addListener(SWT.Dispose, comboListener);
		combo.addListener(SWT.KeyDown, comboListener);
		combo.addListener(SWT.MouseMove, comboListener);
		combo.addListener(SWT.MouseHover, comboListener);
	}

	class LabelListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			Label label = (Label) event.widget;
			shell = label.getShell();
			switch (event.type) {
			case SWT.MouseDown:
			case SWT.KeyDown:
				Event e = new Event();
				e.item = (TableItem) label.getData("_ITEM");
				// Assuming table is single select, set the selection as if
				// the mouse down event went through to the table
				// table.setSelection(new TableItem[] { (TableItem) e.item });
				// combo.getS
				// table.notifyListeners(SWT.Selection, e);
				combo.notifyListeners(SWT.Selection, e);
				// fall through
			case SWT.MouseExit:
			case SWT.KeyUp:
				shell.dispose();
				break;
			}
		}

	}

	class ComboListener implements Listener {

		private Shell tip = null;

		private Label label = null;

		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Dispose:
				// case SWT.KeyDown:
			case SWT.MouseMove: {
				if (tip == null)
					break;
				tip.dispose();
				tip = null;
				label = null;
				break;
			}
			case SWT.KeyDown: {
				String item = combo.getText();
				// TableItem item = table.getItem(new Point(event.x, event.y));

				if (item != null) {
					if (tip != null && !tip.isDisposed())
						tip.dispose();
					tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
					tip.setLayout(new FillLayout());
					label = new Label(tip, SWT.NONE);
					Display display = Display.getCurrent();
					label.setForeground(display
							.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
					label.setBackground(display
							.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
					label.setData("_ITEM", item);
					label.setText("ENTER save changes, other operations discard them");
					LabelListener labelListener = new LabelListener();
					label.addListener(SWT.MouseExit, labelListener);
					label.addListener(SWT.MouseDown, labelListener);
					Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);

					Point pt = null;

					if (event.type == SWT.KeyDown) {
						int estimateWidthPerChar = 4;
						double weight = 1.5;
						int addUp = (int) (combo.getText().length() * estimateWidthPerChar * weight);
						pt = new Point(event.x + addUp + 30, event.y);
					} else {
						pt = new Point(event.x, event.y);
					}
					pt = combo.toDisplay(pt);

					// Rectangle rect = display.getCursorControl().getBounds();
					// Rectangle rect = item.getBounds(0);
					// Point pt = new Point(rect.x, rect.y);
					// Point pt = table.toDisplay(rect.x, rect.y);
					tip.setBounds(pt.x, pt.y, size.x, size.y);
					tip.setVisible(true);
				}
			}
			}
		}

	}

}
