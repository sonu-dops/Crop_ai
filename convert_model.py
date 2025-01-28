import tensorflow as tf
import joblib
import numpy as np

# Load the saved RandomForest model
rf_model = joblib.load('random_forest_crop_model.pkl')

# Create a TensorFlow model that wraps the RandomForest
class CropPredictor(tf.Module):
    def __init__(self, model):
        self.model = model
    
    @tf.function(input_signature=[tf.TensorSpec(shape=[1, 7], dtype=tf.float32)])
    def predict(self, x):
        prediction = self.model.predict(x)
        return tf.constant(prediction)

# Create and save TFLite model
predictor = CropPredictor(rf_model)
converter = tf.lite.TFLiteConverter.from_keras_model(predictor)
tflite_model = converter.convert()

# Save the model
with open('crop_prediction_model.tflite', 'wb') as f:
    f.write(tflite_model) 