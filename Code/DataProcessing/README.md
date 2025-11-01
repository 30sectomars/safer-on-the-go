# Data Processing – LabVIEW Tools

This folder contains the LabVIEW-based tools used for data preprocessing and visualization during the *Safer on the Go* outdoor study.  
These tools were developed to inspect, clean, and convert raw data recorded by the Android application into a format suitable for further statistical analysis in RStudio.

---

## Environment

- **Software:** LabVIEW 2025 Q1 (Version 25.1.3f3, 64-bit)  
- **Additional Package:** [OpenG Toolkit](https://www.vipm.io/package/openg.org_lib_openg_toolkit/) installed via NI Package Manager (VIPM)

---

## Purpose

The LabVIEW VIs in this folder serve the following main purposes:

1. **Data Import**  
   Reading raw `.csv` files recorded by the Android application during the outdoor study.

2. **Data Preprocessing**  
   - Filtering and restructuring the datasets  
   - Merging session data with timestamps and event logs  
   - Preparing unified exports for R-based analysis

3. **Data Visualization**  
   - Inspection of collected data  
   - Quick validation of collected data  
   - Identification of potential recording errors

---

## Notes

- All paths in the VIs are **relative** to the project directory.    
- The output datasets were later analyzed using **R (RStudio)** as part of the statistical evaluation.
