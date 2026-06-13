package com.example.mediai.data.remote;

public class AiRepositoryRemote {

    public static String getDemoResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("ibuprofeno") || lowerMessage.contains("ibuprofeno")) {
            return "El ibuprofeno es un antiinflamatorio no esteroideo (AINE). " +
                    "Se recomienda tomarlo con alimentos para evitar molestias estomacales. " +
                    "Recuerda respetar el intervalo entre dosis indicado en tu receta. " +
                    "Si tienes dudas sobre la dosis o frecuencia, consulta a tu médico.";
        }

        if (lowerMessage.contains("olvid") || lowerMessage.contains("olvide") || lowerMessage.contains("olvidado") || lowerMessage.contains("olvidé")) {
            return "Si olvidaste tomar una dosis, no dupliques la siguiente. " +
                    "Tómala tan pronto lo recuerdes, solo si falta suficiente tiempo para la siguiente dosis. " +
                    "Si ya casi es la hora de la siguiente dosis, omite la olvidada y continúa con tu horario normal. " +
                    "Ante cualquier duda, revisa las indicaciones de tu medicamento o consulta a un profesional de salud.";
        }

        if (lowerMessage.contains("recordatorio") || lowerMessage.contains("recordar") || lowerMessage.contains("alarma")) {
            return "En Medi AI puedes agregar un medicamento con la hora y frecuencia deseada, " +
                    "y la aplicación te enviará un recordatorio automático. " +
                    "Ve a la sección 'Agregar' desde el menú inferior y completa los datos del medicamento. " +
                    "¡Mantener tus recordatorios al día te ayudará a no olvidar ninguna dosis!";
        }

        if (lowerMessage.contains("amoxicilina") || lowerMessage.contains("antibiótico") || lowerMessage.contains("antibiotico")) {
            return "Los antibióticos como la amoxicilina deben tomarse exactamente como lo indicó tu médico. " +
                    "Es importante completar el tratamiento aunque te sientas mejor. " +
                    "No suspendas ni cambies la dosis sin consultar a un profesional. " +
                    "Si presentas reacciones alérgicas (erupciones, hinchazón, dificultad para respirar), busca atención médica urgente.";
        }

        if (lowerMessage.contains("vitamina") || lowerMessage.contains("suplemento")) {
            return "Las vitaminas y suplementos pueden apoyar tu salud, pero no reemplazan una dieta balanceada ni medicamentos recetados. " +
                    "Sigue las indicaciones de tu médico o las instrucciones del producto. " +
                    "Si tienes dudas sobre la dosis adecuada, consulta a un profesional de salud.";
        }

        if (lowerMessage.contains("dolor") || lowerMessage.contains("síntoma") || lowerMessage.contains("sintoma") ||
                lowerMessage.contains("fiebre") || lowerMessage.contains("mareo") || lowerMessage.contains("nausea")) {
            return "Los síntomas pueden tener muchas causas. Si presentas malestar, fiebre, dolor intenso o cualquier síntoma persistente, " +
                    "te recomiendo consultar a un médico para una evaluación adecuada. " +
                    "Medi AI puede ayudarte a organizar tus medicamentos, pero no reemplaza una consulta médica. " +
                    "Si los síntomas son graves, busca atención médica de inmediato.";
        }

        // Default response
        return "Gracias por tu consulta. Medi AI está aquí para ayudarte a organizar tus medicamentos y recordatorios. " +
                "Puedes preguntarme sobre:\n" +
                "• Cómo tomar un medicamento específico\n" +
                "• Qué hacer si olvidaste una dosis\n" +
                "• Cómo configurar recordatorios\n" +
                "• Información general sobre medicamentos\n\n" +
                "Recuerda: Medi AI brinda información general y no sustituye la consulta médica profesional. " +
                "Ante emergencias, acude al centro de salud más cercano.";
    }
}