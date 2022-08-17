package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.io.Serializable;

@SessionScoped
@FacesValidator("toDoValidator")
public class ToDoValidator implements Validator<String>, Serializable {
    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (value.isEmpty()) {
            setComponentInvalid(context, component, "ToDo text can not be empty");
        }
        if (value.length() > 140) {
            setComponentInvalid(context, component, "Maximum ToDo length is 140 characters");
        }
    }

    private static void setComponentInvalid(FacesContext context, UIComponent component, String messageText) {
        ((UIInput) component).setValid(false);
        FacesMessage message = new FacesMessage(messageText);
        context.addMessage(component.getClientId(context), message);
    }
}
