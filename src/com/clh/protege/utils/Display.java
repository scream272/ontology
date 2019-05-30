package com.clh.protege.utils;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.util.Date;

import static com.clh.protege.Main.query;

public class Display extends Frame implements WindowListener {
    private static final long serialVersionUID = -2768217391703339722L;

    private static TextField txtQuestion;
    private static TextArea textResult;
    private static String Question;
    public void showFrame() {

        // 为窗口添加表单控件
        Question = "导致爆炸发生的原因有哪些";
        txtQuestion = new TextField("");
        txtQuestion.setText(Question);
        txtQuestion.setBounds(100, 77, 600, 41);

        Button btnNewButton = new Button("");
        btnNewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Question = txtQuestion.getText();
                System.out.println("Question is " + Question);
                String result = query(Question);
                System.out.println("Result is " + result);
                textResult.setText(result);
            }
        });
        btnNewButton.setBounds(700, 75, 103, 43);
        btnNewButton.setLabel("Query");

        textResult = new TextArea("");
        textResult.setBounds(100, 124, 600, 400);
        Label label = new Label("");
        label.setBounds(15, 60, 60, 34);
        label.setFont(new Font("楷体", 1, 13));
        label.setText("Question");

        Label label_1 = new Label("");
        label_1.setBounds(15, 127, 60, 34);
        label_1.setFont(new Font("楷体", 1, 13));
        label_1.setText("Result");

        this.add(label);
        this.add(label_1);
        this.add(txtQuestion);
        this.add(textResult);
        this.add(btnNewButton);

        this.setLayout(null);
        this.setSize(900, 600);
        this.setLocation(400, 100);
        this.setVisible(true);
        this.setTitle("基于本体的信息查询工具");

        this.addWindowListener(this);

    }

    public void messageBox(String msg) {
        final Dialog d = new Dialog(this, "提示信息", true);//弹出的对话框
        d.setBounds(400, 200, 350, 150);//设置弹出对话框的位置和大小
        d.setLayout(new FlowLayout());//设置弹出对话框的布局为流式布局
        Label lab = new Label(msg);//创建lab标签填写提示内容
        Button okBut = new Button("确定");//创建确定按钮

        d.add(lab);//将标签添加到弹出的对话框内
        d.add(okBut);//将确定按钮添加到弹出的对话框内。

        // 确定按钮监听器
        okBut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                d.setVisible(false);
            }

        });

        // 对话框监听器
        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                d.setVisible(false);//设置对话框不可见

            }

        });
        d.setVisible(true);// 显示
    }


    public void windowClosing(WindowEvent e) {
        System.out.println("Closing");
        System.exit(0); //点击右上角关闭按钮
    }

    public void windowActivated(WindowEvent e1) {
        System.out.println("Activated"); //每次窗口被激活
    }

    public void windowOpened(WindowEvent e2) {
        System.out.println("Open"); //每次打开窗口,
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("Close"); //关闭后事件，不一定激活

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }
}