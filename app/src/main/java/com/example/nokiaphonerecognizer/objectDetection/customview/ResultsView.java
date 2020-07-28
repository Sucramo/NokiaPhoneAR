package com.example.nokiaphonerecognizer.objectDetection.customview;

import com.example.nokiaphonerecognizer.objectDetection.tflite.Classifier;

import java.util.List;



public interface ResultsView {
    public void setResults(final List<Classifier.Recognition> results);
}
