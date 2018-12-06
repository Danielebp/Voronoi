import PIL.ImageDraw as ImageDraw
import PIL.Image as Image
from random import random
import math

sizeX, sizeY = 300.0, 300.0
image = Image.new("RGB", (int(sizeX), int(sizeY)))
draw = ImageDraw.Draw(image)

with open("results.txt") as voronoi:
    for line in voronoi:
        pointStr, polygonStr = line.split("\t")
        point = (float(pointStr.split()[0]), float(pointStr.split()[1]))
        print(point)
        polygonStr = polygonStr.split(";")
        polygon = [(float(pointStr.split()[0]), sizeY - float(pointStr.split()[1])) for pointStr in polygonStr]
        print(polygon)

        r=int(random() * 255)
        g=int(random() * 255)
        b=int(random() * 255)

        y = sizeY - point[1]
        draw.polygon(polygon, fill=(r, g, b))
        draw.ellipse((max(0, math.floor(point[0] - 3.0)),
        			  max(0, math.floor(y - 3.0)),
        			  min(math.floor(sizeX), math.floor(point[0] + 3.0)),
        			  min(math.floor(sizeY), math.floor(y + 3.0))), fill='black')#(r+20, g+20, b+20))

image.show()
