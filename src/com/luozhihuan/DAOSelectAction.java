package com.luozhihuan;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luozhihuan on 16/6/26.
 */
public class DAOSelectAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiMethod psiMethod = getPsiMethodFromContext(e);
        generateInsertMethod(psiMethod);
    }


    private void generateInsertMethod(final PsiMethod psiMethod) {
        new WriteCommandAction.Simple(psiMethod.getProject(), psiMethod.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                createInsert(psiMethod);
            }
        }.execute();
    }


    private void createInsert(PsiMethod psiMethod) {
        String methodText = psiMethod.getText();
        int firstBrac = methodText.indexOf("{");
        methodText = methodText.substring(0, firstBrac + 1);
        String content = "{content}";
        methodText += "\n  " + content + "\n}\n";
        PsiType returnType = psiMethod.getReturnType();
//        if (returnType == null) {
//            return;
//        }
        String packageReturnName = returnType.getCanonicalText();
        String returnClassName = returnType.getPresentableText();
        //入参信息
        PsiParameter psiParameter = psiMethod.getParameterList().getParameters()[0];
        //带package的class名称
        String parameterClassWithPackage = psiParameter.getType().getInternalCanonicalText();
        //为了解析字段，这里需要加载参数的class
        JavaPsiFacade facade = JavaPsiFacade.getInstance(psiMethod.getProject());
        //获取入参信息
        PsiClass parameterClass = facade.findClass(parameterClassWithPackage, GlobalSearchScope.allScope(psiMethod.getProject()));
//        if (parameterClass == null) {
//            return;
//        }
        //获取返回值信息
        PsiClass returnClass = facade.findClass(packageReturnName, GlobalSearchScope.allScope(psiMethod.getProject()));
//        if (parameterClass == null) {
//            return;
//        }
        String methodContent = getMethodText(psiParameter.getName(), parameterClass, returnClass);
        methodText = methodText.replace(content, methodContent);
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        PsiMethod toMethod = elementFactory.createMethodFromText(methodText, psiMethod);
        psiMethod.replace(toMethod);
    }


    private String getMethodText(String parameterName, PsiClass parameterClass, PsiClass returnClass) {
        StringBuilder builder = new StringBuilder();
        builder.append("org.springframework.jdbc.core.RowMapper<" + returnClass.getName() + "> ROW_MAPPER = ((rs, i) -> {");
        builder.append(Constants.BLANK);
        String domainName = StatementUtils.makeFirstWordLow(StatementUtils.deleteDO(returnClass.getName()));
        builder.append(returnClass.getName() + " " + domainName + " = new " + returnClass.getName() + "();");
        builder.append(Constants.BLANK);
        PsiField[] parameterClassAllFields = returnClass.getAllFields();
        for (PsiField parameterClassAllField : parameterClassAllFields) {
            String type = parameterClassAllField.getType().getCanonicalText();
            if (type.equals("java.lang.Long") || type.equals("long")) {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getLong(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            } else if (type.equals("java.lang.String")) {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getString(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            } else if (type.equals("java.lang.Integer") || type.equals("int")) {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getInt(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            } else if (type.equals("java.util.Date")) {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getDate(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            } else if (type.equals("java.lang.Boolean") || type.equals("boolean")) {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getBoolean(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            } else {
                builder.append(domainName + ".set" + StatementUtils.makeFirstWordUpper(parameterClassAllField.getName()) + "(rs.getXx(\"" + StatementUtils.convertToTableFieldName(parameterClassAllField.getName()) + "\"));");
            }
            builder.append(Constants.BLANK);
        }
        builder.append("return " + domainName + ";");
        builder.append("});");

        builder.append(Constants.BLANK);
        List<String> fieldNameList = new ArrayList();
        for (PsiField parameterClassAllField : parameterClassAllFields) {
            if (!parameterClassAllField.getName().equals("id")) {
                fieldNameList.add(parameterClassAllField.getName());
            }
        }
        builder.append("return db.queryForObject(");
        builder.append(Constants.BLANK);
        builder.append("\"select id, \" +");
        builder.append(Constants.BLANK);
        for (int i = 0; i < fieldNameList.size(); i++) {
            String fieldName = fieldNameList.get(i);
            if (i == fieldNameList.size() - 1) {
                builder.append("\"" + StatementUtils.convertToTableFieldName(fieldName) + " \" +");
            }else {
                builder.append("\"" + StatementUtils.convertToTableFieldName(fieldName) + ", \" +");
            }
            builder.append(Constants.BLANK);
        }
        builder.append("\"from `" + StatementUtils.tableName(domainName) + "` where `id` = :id\", java.util.Collections.singletonMap(\"id\", id), ROW_MAPPER);");
        builder.append(Constants.BLANK);
        return builder.toString();
    }


    private PsiMethod getPsiMethodFromContext(AnActionEvent e) {
        PsiElement elementAt = getPsiElement(e);
        if (elementAt == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }
        //用来获取当前光标处的PsiElement
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

}
