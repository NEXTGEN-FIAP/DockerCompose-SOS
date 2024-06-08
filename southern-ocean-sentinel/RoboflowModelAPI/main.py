import base64

import cv2
import numpy as np
from fastapi import FastAPI, Request
from pydantic import BaseModel
from roboflow import Roboflow

app = FastAPI()

rf = Roboflow(api_key="rx4eN0XVSONmEKZ4xvxX")
project = rf.workspace().project("krill")
model = project.version(3).model

class ImageRequest(BaseModel):
    image: str


@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/roboflow-model")
async def use_roboflow_model(request: Request):
    try:
        data = await request.json()
        image_base64 = data.get("image")
        if not image_base64:
            return {"error": "No image data found in request"}

        # Convert base64 to image
        img_data = base64.b64decode(image_base64)
        nparr = np.frombuffer(img_data, np.uint8)
        img_np = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        # Save the image to a temporary file
        temp_filename = "temp.jpg"
        cv2.imwrite(temp_filename, img_np)

        # Infer on the image
        prediction = model.predict(temp_filename).json()

        result = {"message": "Image processed successfully", "prediction": prediction}

        return result
    except Exception as e:
        return {"error": str(e)}