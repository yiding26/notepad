package yiding.text.view.component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

public class FileItemViewCellRender implements ListCellRenderer<Object> {
    public FileItemViewCellRender() {
        super();
    }
    /**
     * @param list，持有该render的JList对象
     * @param value，JList中的每一项，即JList<AType>中的AType类型的对象，它是通过调用list.getModel().getElementAt(index)方法获得的
     * @param index，项的索引
     * @param isSelected，该项是否被选中
     * @param cellHasFocus，该项是否获得焦点
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        File file = (File) value;
        Box panel = Box.createHorizontalBox();
        Label fileIcon;
        try {
            fileIcon = new Label(FileSystemView.getFileSystemView().getSystemIcon(file));
        } catch (Exception e) {
            fileIcon = new Label("");
        }
        panel.add(fileIcon);
        panel.add(new JLabel(file.getName()));
        panel.add(Box.createHorizontalGlue());
        panel.setPreferredSize(new Dimension(10, 27));
        if (isSelected) panel.setBackground(Color.CYAN);
        if (cellHasFocus) {
            panel.setBorder(new LineBorder(Color.CYAN, 4, false));
        }
        return panel;
    }
}