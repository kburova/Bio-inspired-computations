# Particle Swarm Optimization Algorithm
# Written by Ksenia Burova

from random import uniform
import math
import copy
# import libraries for ploting
import numpy as np
import os
import matplotlib.pyplot as plt

#global value that don't change
worldWidth = 100
worldHeight = 100
threshold = 0.01
epsilon = 0.001

class Position:
    def __init__(self, x, y):
        self.x = x
        self.y = y

class Particle:
    def __init__(self, p, v, bp):
        self.p = p
        self.v = v
        self.bp = bp
        self.fitness = 0.0
        self.neighborhood = []

    def updateFitness(self,fitness):
        self.fitness = fitness

    def updateVelocityAndPosition(self, i,c,s,swarmBest, maxVelocity):
        r1 = uniform(0, 1)
        r2 = uniform(0, 1)
        r3 = uniform(0, 1)
        localBest = self
        if self.neighborhood[0].fitness > localBest.fitness:
            localBest = self.neighborhood[0]
        elif self.neighborhood[1].fitness > localBest.fitness:
            localBest = self.neighborhood[1]
        self.v.x = i * self.v.x + c * r1 * (self.bp.x - self.p.x) + s * r2 * (swarmBest.x - self.p.x)  #+ 1.5 * r3 * (localBest.p.x - self.p.x)
        self.v.y = i * self.v.y + c * r1 * (self.bp.y - self.p.y) + s * r2 * (swarmBest.y - self.p.y)  #+ 1.5 * r3 * (localBest.p.y - self.p.y)
        #scale velocity if necessary
        distance = math.sqrt( math.pow(self.v.x, 2) + math.pow(self.v.y, 2))
        if math.fabs(self.v.x) > pow(maxVelocity,2):
            self.v.x *= maxVelocity/distance
        if math.fabs(self.v.y) > pow(maxVelocity,2):
            self.v.y *= maxVelocity/distance

        self.p.x += self.v.x
        self.p.y += self.v.y

# class run will Run 1 experiment
class SORun:
    def __init__(self, numOfEpochs, numOfParticles, inertia, cognition, socialparam, problem, maxV):
        self.epochs = numOfEpochs
        self.pNum = numOfParticles
        self.i = inertia
        self.c = cognition
        self.s = socialparam
        self.problem = problem
        self.errors_x = []
        self.errors_y = []
        self.averageFitness = []
        self.averageDistance = []
        self.maxVelocity = maxV
        self.particles = []
        self.maxima = []
        self.converged = 0

        self.maxima.append(Position(20, 7))
        if problem == 2:
            self.maxima.append(Position(-20, -7))

        self.SwarmBest = Position(0, 0)
        self.numOfIterations = 0

    def createFolder(self):
        self.experiment_folder = str(self.pNum) + "_" + str(self.i) + "_" + str(self.c) + "_" + str(self.s) + "_" + str(self.maxVelocity)
        self.problem_folder = "P" + str(self.problem)

        if not os.path.exists(self.problem_folder):
            os.makedirs(self.problem_folder)
        if not os.path.exists(self.problem_folder + "/" + self.experiment_folder):
            os.makedirs(self.problem_folder + "/" + self.experiment_folder)

    def plotScatter(self, iteration):
        plotSwarm(iteration, self)

    def plotError(self, iteration):
        plotErrors(iteration,self)

    def plotFitness(self, iteration):
        plotAveFitness(iteration,self)

    def plotDistance(self, iteration):
        plotAveDistance(iteration,self)

    #use global function depending on problem
    def Q(self,p):
        if self.problem == 1:
            return Q1(p)
        else:
            return Q2(p)

    def setNeiborhood(self):
        for particle in self.particles:
            sortedParticles = sorted(self.particles, key=(lambda neighb: posDiff(particle.p, neighb.p)), reverse=False)
            pair = []
            pair.append(sortedParticles[1])
            pair.append(sortedParticles[2])
            particle.neighborhood = pair

    def initParticles(self):
        for i in range(self.pNum):
            # generate random position
            initPosition = Position(uniform(-worldWidth / 2.0, worldWidth / 2.0),
                                    uniform(-worldHeight / 2.0, worldHeight / 2.0))
            # print initPosition.x, initPosition.y
            particle = Particle(initPosition, Position(0, 0), copy.deepcopy(initPosition))
            particle.updateFitness(self.Q(initPosition))

            self.particles.append(particle)
            # check if best and if so update swarm best value
            if self.Q(initPosition) < self.Q(self.SwarmBest):
                self.SwarmBest = copy.deepcopy(initPosition)

    def updateParticleBest(self, particle):
        particle.updateFitness(self.Q(particle.p))

        if self.Q(particle.p) > self.Q(particle.bp):
            particle.bp = copy.deepcopy(particle.p)
            if self.Q(particle.p) > self.Q(self.SwarmBest):
                self.SwarmBest = copy.deepcopy(particle.p)

    def converge(self):
        while True:
            error_x = 0.0
            error_y = 0.0
            for particle in self.particles:
                particle.updateVelocityAndPosition(self.i, self.c, self.s, self.SwarmBest, self.maxVelocity)
                self.updateParticleBest(particle)

            for particle in self.particles:
                error_x += (particle.p.x - self.SwarmBest.x) ** 2
                error_y += (particle.p.y - self.SwarmBest.y) ** 2

            error_x = math.sqrt(error_x / (2 * self.pNum))
            error_y = math.sqrt(error_y / (2 * self.pNum))
            self.errors_x.append(error_x)
            self.errors_y.append(error_y)
            self.averageFitness.append( self.getAveargeFitness() )
            self.averageDistance.append( self.getAverageDistance() )
            self.numOfIterations += 1
            # print numOfIterations, error_x, error_y

            if (self.numOfIterations > self.epochs):
                return False

            if (error_x < threshold and error_y < threshold):
                return True

    def getConvergedParticles(self):
        for particle in self.particles:
            #particle will converge to one or another maximum but not to both
            for pos in self.maxima:
                if posDiff(particle.p,pos) <= epsilon:
                    self.converged += 1
        self.converged /= self.pNum * 1.0

    def getAverageDistance(self):
        for particle in self.particles:
            distance = 100
            for pos in self.maxima:
                d = posDiff(particle.p,pos)
                if d < distance:
                    distance = d
        return d/self.pNum

    def getAveargeFitness(self):
        fit = 0.0
        for particle in self.particles:
           fit += particle.fitness
        return fit/self.pNum

    def run(self):
        self.initParticles()
        self.createFolder()
        self.plotScatter(self.numOfIterations)
        self.setNeiborhood()
        flag = self.converge()
        self.getConvergedParticles()
        self.plotScatter(self.numOfIterations)
        self.plotError(self.numOfIterations)
        self.plotFitness(self.numOfIterations)
        self.plotDistance(self.numOfIterations)
        if flag:
            return True
        return False

# Plot Converged particles using X and Y coordinates
def plotSwarm(iteration, object):
    filename = object.problem_folder+"/"+object.experiment_folder+"/Scatter-"+str(iteration)+".png"
    X = []
    Y = []
    for particle in object.particles:
        X.append(particle.p.x)
        Y.append(particle.p.y)

    maxX = []
    maxY = []
    for m in object.maxima:
        maxX.append(m.x)
        maxY.append(m.y)

    figure = plt.figure()
    subplt = plt.subplot(1,1,1,axisbg='#F5F7FC')
    subplt.scatter(X, Y, s=50, marker = "o", c = "#621CEF", alpha=0.5, label = "Particle")
    subplt.scatter(maxX, maxY, s=50, marker = 'o', c = "#04FDF2", label = "Global Best")
    box = subplt.get_position()
    subplt.set_position([box.x0, box.y0, box.width * 0.8, box.height])
    plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
    plt.title(" Converged Particles")
    plt.xlabel("Particle X coordinates")
    plt.ylabel("Particle Y coordinates")
    plt.grid()
    figure.savefig(filename)
    plt.close()
# Plot error graphs
def plotErrors(iteration, object):
    sz = 5
    if iteration >= 50:
        sz = 3
    elif iteration >= 100:
        sz = 1.5
    elif iteration >= 150:
        sz = 1.0
    filename = object.problem_folder + "/" + object.experiment_folder + "/Errors-" + str(iteration) + ".png"
    x = range(0,iteration)
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')
    subplt.plot(x, object.errors_x, markersize=sz, marker='o', linestyle='--', color='#03BCF8', label='X Error', linewidth = 2.0)
    subplt.plot(x, object.errors_y, markersize=sz, marker='o', linestyle='--', color='#F80372', label='Y Error', linewidth = 2.0)
    plt.xlabel('Number of Epochs')
    plt.ylabel('Error value')
    plt.title('Distance Error')
    plt.legend()
    plt.grid()
    figure.savefig(filename)
    plt.close()
# Plot Fitness graphs
def plotAveFitness(iteration, object):
    sz = 5
    if iteration >= 50:
        sz = 3
    elif iteration >= 100:
        sz = 1.5
    elif iteration >= 150:
        sz = 1.0
    filename = object.problem_folder + "/" + object.experiment_folder + "/Fitness-" + str(iteration) + ".png"
    x = range(0,iteration)
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')
    subplt.plot(x, object.averageFitness, markersize=sz, marker='o', linestyle='--', color='#03D3F8', label='Avg Fitness', linewidth = 2.0)
    plt.xlabel('Number of Epochs')
    plt.ylabel('Fitness value')
    plt.title('Average Fitness')
    plt.legend(loc=4)
    plt.grid()
    figure.savefig(filename)
    plt.close()
# Plot Fitness graphs
def plotAveDistance(iteration, object):
    sz = 5
    if iteration >= 50:
        sz = 3
    elif iteration >= 100:
        sz = 1.5
    elif iteration >= 150:
        sz = 1.0
    filename = object.problem_folder + "/" + object.experiment_folder + "/Distance-" + str(iteration) + ".png"
    x = range(0,iteration)
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')
    subplt.plot(x, object.averageDistance,markersize=sz, marker='o', linestyle='--', color='#F59549', label='Avg Distance', linewidth = 2.0)
    plt.xlabel('Number of Epochs')
    plt.ylabel('Distance value')
    plt.title('Average Distance To Global Max')
    plt.legend()
    plt.grid()
    figure.savefig(filename)
    plt.close()
#global functions for fitness
def mdist():
    return math.sqrt(math.pow(worldWidth,2) + math.pow(worldHeight, 2)) / 2.0
def pdist (p):
    return math.sqrt(math.pow(p.x-20, 2) + math.pow(p.y-7, 2))
def ndist (np):
    return math.sqrt(math.pow(np.x+20, 2) + math.pow(np.y+7, 2))
def Q1(p):
    return 100 * (1 - pdist(p)/mdist())
def Q2(p):
    return 9*max(0.0, 10 - math.pow(pdist(p),2)) + 10*(1 - pdist(p)/mdist()) + 70*(1 - ndist(p)/mdist())
def posDiff(p1,p2):
    return math.sqrt( math.pow(p1.x - p2.x, 2) + math.pow(p1.y - p2.y, 2))

