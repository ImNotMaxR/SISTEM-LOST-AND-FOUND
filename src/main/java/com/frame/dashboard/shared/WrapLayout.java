package com.frame.dashboard.shared;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= getHgap() + 1;
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = resolveTargetWidth(target);
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (getHgap() * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dimension = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            for (int i = 0; i < target.getComponentCount(); i++) {
                java.awt.Component component = target.getComponent(i);
                if (!component.isVisible()) {
                    continue;
                }

                Dimension componentSize = preferred ? component.getPreferredSize() : component.getMinimumSize();
                if (rowWidth + componentSize.width > maxWidth) {
                    addRow(dimension, rowWidth, rowHeight);
                    rowWidth = 0;
                    rowHeight = 0;
                }

                if (rowWidth != 0) {
                    rowWidth += getHgap();
                }
                rowWidth += componentSize.width;
                rowHeight = Math.max(rowHeight, componentSize.height);
            }

            addRow(dimension, rowWidth, rowHeight);
            dimension.width += horizontalInsetsAndGap;
            dimension.height += insets.top + insets.bottom + (getVgap() * 2);

            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) {
                dimension.width -= getHgap() + 1;
            }

            return dimension;
        }
    }

    private int resolveTargetWidth(Container target) {
        Container container = target;
        while (container.getSize().width == 0 && container.getParent() != null) {
            container = container.getParent();
        }

        int targetWidth = container.getSize().width;
        return targetWidth == 0 ? Integer.MAX_VALUE : targetWidth;
    }

    private void addRow(Dimension dimension, int rowWidth, int rowHeight) {
        dimension.width = Math.max(dimension.width, rowWidth);

        if (dimension.height > 0) {
            dimension.height += getVgap();
        }

        dimension.height += rowHeight;
    }
}