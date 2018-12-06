import sys
import PIL.ImageDraw as ImageDraw
import PIL.Image as Image
from random import random

sizeX, sizeY = int(sys.argv[1]), int(sys.argv[2])
image = Image.new("RGB", (sizeX, sizeY))
draw = ImageDraw.Draw(image)

with open(sys.argv[3]) as voronoi:
    for line in voronoi:
        pointStr, polygonStr = line.split("	")
        point = (float(pointStr.split()[0]), float(pointStr.split()[1]))
        polygonStr = polygonStr.split(";")
        polygon = [(float(pointStr.split()[0]), float(pointStr.split()[1])) for pointStr in polygonStr]

        draw.polygon(polygon, fill=(int(random() * 255), int(random() * 255), int(random() * 255)))
        draw.ellipse((max(0, point[0] - 5), 
        			  max(0, point[1] - 5),
        			  min(sizeX, point[0] + 5), 
        			  min(sizeY, point[1] + 5)), fill = 'black')

image.show()
