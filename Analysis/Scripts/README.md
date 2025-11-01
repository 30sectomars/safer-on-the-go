# Analysis Scripts for RStudio

This folder contains the R scripts used for the statistical analysis of the *Safer on the Go* outdoor study.  
All scripts were developed and executed in **RStudio 2025.09.1 (Build 401)** and are designed to reproduce the main quantitative and qualitative analyses presented in this thesis.

---

## Environment

- **Software:** RStudio 2025.09.1 (Build 401)  
- **R Version:** ≥ 4.4.0  

### Required Packages

| Package | Purpose |
|----------|----------|
| **tidyverse** | Collection of core packages (ggplot2, dplyr, tidyr, purrr, etc.) for data manipulation, cleaning, and visualization. |
| **ARTool** | Performs Aligned Rank Transform (ART) ANOVA for nonparametric factorial analysis. |
| **ggplot2** | Creates publication-quality figures such as boxplots and bar charts used in this thesis. |
| **emmeans** | Computes estimated marginal means and performs pairwise comparisons for post-hoc analyses. |
| **plyr** | Provides tools for splitting, applying, and combining data (predecessor of dplyr, used for compatibility). |
| **tidyr** | Reshapes and structures datasets (wide/long format transformations). |
| **dplyr** | Core package for data wrangling: filtering, summarizing, grouping, and joining data frames. |
| **likert** | Specialized analysis and visualization of Likert-scale questionnaire data. |
| **patchwork** | Combines multiple ggplot2 plots into composite figures for multi-panel visualizations. |
| **purrr** | Functional programming tools for iterative operations and list processing within tidyverse workflows. |

*(Package versions as of April 2025)*

---

## Purpose of Scripts

| Script | Description |
|--------|--------------|
| **`shapiro-test-all-experimental.R`** | Performs Shapiro–Wilk normality tests for all dependent variables across experimental conditions to verify assumptions for ANOVA. |
| **`shapiro-test-all-questions.R`** | Checks normal distribution for questionnaire-related variables (e.g., Likert-scale mean scores). |
| **`Analyse-ART-ANOVA.R`** | Executes the Aligned Rank Transform (ART) ANOVA including main and interaction effects for all dependent variables (e.g., task completion time, error count, etc.). |
| **`Analyse-Eye-Tracker.R`** | Processes and summarizes eye-tracking data such as gaze counts, frame durations, and attention metrics per input method. |
| **`plot-all-experimental.R`** | Generates figures for all experimental variables (e.g., TCT, error count, overshoot, overshoot time). |
| **`plot-overall-questions.R`** | Produces aggregated bar charts summarizing participant feedback questions (e.g., accuratest, easiest, safest method). |
| **`plot-likert.R`** | Creates Likert-scale visualizations for subjective user experience and usability ratings. |
| **`plot-general-questions.R`** | Visualizes additional survey results (e.g., age, gender, experience level) for descriptive statistics. |

---

## Output

- Summary tables (means, SDs, p-values, etc.)  
- Post-hoc contrast results  
- Visualization outputs (bar plots, boxplots, distribution charts)  

---

## Notes

- All scripts assume the preprocessed datasets from the `DataProcessing` (LabVIEW) step.  
- Each script is self-contained and can be run independently after setting the correct working directory.  
- Output tables and plots were directly integrated into this thesis (Results and Discussion chapters).
