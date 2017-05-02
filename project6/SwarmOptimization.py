# Particle Swarm Optimization Algorithm
# Written by Ksenia Burova

import sys
from random import uniform
import math
import copy
# import libraries for ploting
import plotly.plotly as py
import plotly.graph_objs as go
if len(sys.argv) != 7:
    print "Usage: SwarmOptimization.py [NumOfEpochs] [NumOfParticles] [Inertia] [Cognition] [SocialParam] [problem #]"
    exit(0)

numberOfEpochs = int(sys.argv[1])
numberOParticles = int(sys.argv[2])
inertia = float(sys.argv[3])
cognition = float(sys.argv[4])
social = float(sys.argv[5])
problem = int(sys.argv[6])
maxima = []
errors_x = []
errors_y = []
if problem != 1 and problem != 2:
    print "Usage: problem number can be 1 or 2"

worldWidth = 100
worldHeight = 100
maxVelocity = 7
threshold = 0.01

particles = []
class Position:
    def __init__(self, x, y):
        self.x = x
        self.y = y

class Particle:
    def __init__(self, p, v, bp):
        self.p = p
        self.v = v
        self.bp = bp
        self.neighborhood = []

    def updateVelocity(self):
        r1 = uniform(0, 1)
        r2 = uniform(0, 1)
        r3 = uniform(0, 1)
        localBest = self
        if Q(self.neighborhood[0].p) > Q(localBest.p):
            localBest = self.neighborhood[0]
        elif Q(self.neighborhood[1].p) > Q(localBest.p):
            localBest = self.neighborhood[1]
        self.v.x = inertia * self.v.x + cognition * r1 * (self.bp.x - self.p.x) + social * r2 * (SwarmBest.x - self.p.x) + 1.5 * r3 * (localBest.p.x - self.p.x)
        self.v.y = inertia * self.v.y + cognition * r1 * (self.bp.y - self.p.y) + social * r2 * (SwarmBest.y - self.p.y) + 1.5 * r3 * (localBest.p.y - self.p.y)
        #scale velocity if necessary
        distance = math.sqrt( math.pow(self.v.x, 2) + math.pow(self.v.y, 2))
        if math.fabs(self.v.x) > pow(maxVelocity,2):
            self.v.x *= maxVelocity/distance
        if math.fabs(self.v.y) > pow(maxVelocity,2):
            self.v.y *= maxVelocity/distance

    def updatePosition(self):
        self.p.x += self.v.x
        self.p.y += self.v.y

        if Q(self.p) > Q(self.bp):
            self.bp = copy.deepcopy(self.p)
            return True
        return False

def mdist():
    return math.sqrt(math.pow(worldWidth,2) + math.pow(worldHeight, 2)) / 2.0
def pdist (p):
    return math.sqrt(math.pow(p.x-20, 2) + math.pow(p.y-7, 2))
def ndist (np):
    return math.sqrt(math.pow(np.x+20, 2) + math.pow(np.y+7, 2))
def Q1(p):
    maxima.append(Position(20,7))
    return 100 * (1 - pdist(p)/mdist())
def Q2(p):
    maxima.append(Position(20,7))
    maxima.append(Position(-20,-7))
    return 9*max(0.0, 10 - math.pow(pdist(p),2)) + 10*(1 - pdist(p)/mdist()) + 70*(1 - ndist(p)/mdist())
def Q(p):
    if problem == 1:
        return Q1(p)
    else:
        return Q2(p)

def setNeiborhood():
    for particle in particles:
        sortedParticles = sorted(particles, key = (lambda neighb: posDiff(particle.p, neighb.p)), reverse = False)
        pair = []
        pair.append(sortedParticles[1])
        pair.append(sortedParticles[2])
        particle.neighborhood = pair

def posDiff(p1,p2):
    return math.sqrt( math.pow(p1.x - p2.x, 2) + math.pow(p1.y - p2.y, 2))

SwarmBest = Position(0, 0)
for i in range(numberOParticles):
    #generate random position
    initPosition = Position(uniform(-worldWidth/2.0, worldWidth/2.0), uniform(-worldHeight/2.0, worldHeight/2.0))
    # print initPosition.x, initPosition.y
    particle = Particle(initPosition, Position(0,0), copy.deepcopy(initPosition))
    if particles.append(particle):
        #check if best and if so update swarm best value
        if Q(initPosition) < Q(SwarmBest):
            SwarmBest = copy.deepcopy(initPosition)
numOfIterations = 0
setNeiborhood()

while True:
    error_x = 0.0
    error_y = 0.0
    for particle in particles:
        particle.updateVelocity()
        particle.updatePosition()
        if Q(particle.p) > Q(SwarmBest):
            SwarmBest = copy.deepcopy(particle.p)
    # print "SwarmBest", SwarmBest.x, SwarmBest.y
    for particle in particles:
        error_x += (particle.p.x - SwarmBest.x)**2
        error_y += (particle.p.y - SwarmBest.y)**2

    error_x = math.sqrt(error_x / (2 * numberOParticles))
    error_y = math.sqrt(error_y / (2 * numberOParticles))
    errors_x.append(error_x)
    errors_y.append(error_y)

    numOfIterations += 1
    print numOfIterations, error_x, error_y

    if (error_x < threshold and error_y < threshold):
        break

trace = go.Scatter(
    x = errors_x,
    y = errors_y,
    mode = 'markers'
)

data = [trace]
py.iplot(data, filename='basic-scatter')