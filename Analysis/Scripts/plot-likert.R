library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)
library(ggplot2)
library(likert)
source("HelperScripts/summary.r")
source("HelperScripts/grid_arrange_shared_legend.r")

data.raw <- read.delim("../all_questionair_merged.txt", header = T)
data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Question <- factor(data.raw$Question)
data.raw$QuestionType <- factor(data.raw$QuestionType)
data.raw$Method <- factor(data.raw$Method)

data.likert <- subset(data.raw, QuestionType == "Likert")
data.likert <- subset(data.likert, Question != "Did you feel that using touch input required you to look away from the road/environment too much?")
data.likert$Method <- factor(data.likert$Method)

likert.questions <- unique(data.likert$Question)

plots <- list()
for (q in likert.questions) {
  cat("\n=== Question:", q, "===\n")
  
  qdata <- subset(data.likert, Question == q)
  var <- as.numeric(qdata$Answer)
  
  cat("\n--- Means & SDs by Method ---\n")
  for (method in levels(data.likert$Method)) {
    m <- mean(var[qdata$Method == method], na.rm = TRUE)
    s <- sd(var[qdata$Method == method], na.rm = TRUE)
    cat(sprintf("Method == '%s': Mean = %.3f, SD = %.3f\n", method, m, s))
  }
  
  likert_labels <- c("Strongly Disagree", "Disagree", "Neither", "Agree", "Strongly Agree")
  qdata$Condition <- interaction(qdata$Method)
  
  qspread <- qdata %>%
    dplyr::select(ProbID, Condition, Answer) %>%
    tidyr::spread(key = Condition, value = Answer)
  for (col in colnames(qspread)[-1]) {
    qspread[[col]] <- factor(qspread[[col]], levels = 1:5, labels = likert_labels)
  }
  qspread <- qspread[rowSums(is.na(qspread[,-1])) < ncol(qspread)-1, ]
  
  if (ncol(qspread) > 1) {
    likert_obj <- likert::likert(qspread[,-1])
    
    if (q == "How safe did you feel while operating the device?" || q == "The remote control did not interfere with my grip or steering.") {
      p_likert <- plot(likert_obj, type = "bar", text.size = 8, ordered = FALSE) +
        ggtitle(paste0(q)) +
        theme(
          plot.title    = element_text(size = 26, face = "bold"), 
          text          = element_text(size = 20),
          axis.text.y   = element_text(size = 20),
          axis.title.x   = element_blank(),
          legend.title  = element_blank(),
          legend.text = element_text(size=30)
        )
    }
    else if (q == "The input felt physically effortless.") {
      p_likert <- plot(likert_obj, type = "bar", text.size = 8, ordered = FALSE) +
        ggtitle("The input felt physically effortless.") +
        theme(
          plot.title    = element_text(size = 26, face = "bold"), 
          text          = element_text(size = 20),
          axis.text.y   = element_text(size = 20),
          axis.title.x   = element_blank(),
          legend.title  = element_blank(),
          legend.text = element_text(size=20)
        )
    }
    else {
      p_likert <- plot(likert_obj, type = "bar", text.size = 8, ordered = FALSE) +
        ggtitle(paste0(q)) +
        theme(
          plot.title    = element_text(size = 26, face = "bold"), 
          text          = element_text(size = 20),
          axis.text.y   = element_blank(), 
          axis.title.x   = element_blank(), 
          legend.title  = element_blank(),
          legend.text = element_text(size=20)
        )
    }
    
    
    print(p_likert)
    plots[[q]] <- p_likert
  }
}

data.combinedplot.all <- do.call(grid_arrange_shared_legend, c(plots[1:6], ncol = 3, nrow = 2))
ggsave(file.path("likert_all.pdf"), plot=data.combinedplot.all, width = 47, height = 10, limitsize = FALSE)

data.combinedplot.remote <- do.call(grid_arrange_shared_legend, c(plots[7:8], ncol = 2, nrow = 1))
ggsave(file.path("likert_remote.pdf"), plot=data.combinedplot.remote, width = 32, height = 10, limitsize = FALSE)
