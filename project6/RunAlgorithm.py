# Particle Swarm Optimization Algorithm Runs
# Written by Ksenia Burova

import sys
import os
import SwarmOptimization as so
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) != 6:
    print "Usage: RunAlgorithm.py [NumOfEpochs] [NumOfParticles] [Inertia] [Cognition] [SocialParam]"
    exit(0)

numberOfEpochs = int(sys.argv[1]) # 1000
numberOParticles = int(sys.argv[2]) # 100
inertia = float(sys.argv[3]) # 0.1
cognition = float(sys.argv[4]) # 0.5
social = float(sys.argv[5]) # 0.5
velocity = 7

colors = [ "#F90F04", "#FE8802", "#FEEB02", "#65FE02", "#0DC759", "#06D5BF","#03D6FC", "#0369FC", "#8755E6", "#EC1EF2" ]

particlesValues = [30, 50, 100]
partEpochsToConv = []
partAveDistance = []
partConverged = []
partFitness = []

inertiaValues = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]
inertiaEpochsToConv = []
inertiaAveDistance = []
inertiaConverged = []
inertiaFitness = []

cognitionValues = [0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0]
cogEpochsToConv = []
cogAveDistance = []
cogConverged = []
cogFitness = []

socialValues = [0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0]
socEpochsToConv = []
socAveDistance = []
socConverged = []
socFitness = []

velocityValues = [1, 3, 5, 7, 10]
velEpochsToConv = []
velAveDistance = []
velConverged = []
velFitness = []

#default Run
# pso = so.SORun(numberOfEpochs, numberOParticles, inertia, cognition, social, 2, 7)
# pso.run()

# BAR graph
def plotEpochsToConvergence( x, bars, valToChange):
    filename = experiment_folder + "/Epochs.png"
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')
    y_pos = np.arange( len(x) )
    subplt.bar(y_pos, bars, align="center", alpha=0.5)
    plt.xticks(y_pos, x)
    plt.ylabel("Number Of Epochs")
    plt.xlabel( valToChange )
    plt.title("Number Of Epochs To Convergence")
    plt.grid()
    figure.savefig(filename)
    plt.close()

# BAR graph
def plotConvergedPercentage(x, bars, valToChange):
    filename = experiment_folder + "/ConvergePercent.png"
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')
    y_pos = np.arange(len(x))
    subplt.bar(y_pos, bars, align="center", alpha=0.5)
    plt.xticks(y_pos, x)
    plt.ylabel("Particles Percent")
    plt.xlabel(valToChange)
    plt.title("Percent of Particles Converged 0.001 from Max")
    plt.grid()
    figure.savefig(filename)
    plt.close()

# Plot Fitness graph
def plotAveFitness( X, vectors, valToChange):
    sz = 1.0
    filename = experiment_folder + "/Fitness.png"
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')

    for index, vector in enumerate(vectors):
        x = range(0, len(vector))
        subplt.plot(x, vector, markersize=sz, marker='o', linestyle='-', color=colors[index],
                    label=valToChange+"="+str(X[index]), linewidth = 1.0)
    plt.xlabel('Number of Epochs')
    plt.ylabel('Fitness value')
    plt.title('Average Fitness')
    plt.legend(loc=4)
    plt.grid()
    figure.savefig(filename)
    plt.close()
# Plot Distance graph
def plotAveDistance(X, vectors, valToChange):
    sz = 1.0
    filename = experiment_folder + "/Distance.png"
    figure = plt.figure()
    subplt = plt.subplot(1, 1, 1, axisbg='#F5F7FC')

    for index, vector in enumerate(vectors):
        x = range(0, len(vector))
        subplt.plot(x, vector, markersize=sz, marker='o', linestyle='-', color=colors[index],
                    label=valToChange + "=" + str(X[index]), linewidth=1.0)

    plt.xlabel('Number of Epochs')
    plt.ylabel('Distance value')
    plt.title('Average Distance To Global Max')
    plt.legend()
    plt.grid()
    figure.savefig(filename)
    plt.close()

def createFolder(folder):
    if not os.path.exists(folder):
        os.makedirs(folder)


for problem in range(1, 3):
    problem_folder = "P" + str(problem)

    # number of particles
    partEpochsToConv = []
    partAveDistance = []
    partConverged = []
    partFitness = []
    experiment_folder = problem_folder + "/numOfParticles"
    createFolder(experiment_folder)

    for val in particlesValues:
        pso = so.SORun(numberOfEpochs, val, inertia, cognition, social, problem, velocity)
        pso.run()
        partEpochsToConv.append(pso.numOfIterations - 1)
        partAveDistance.append(pso.averageDistance)
        partConverged.append(pso.converged)
        partFitness.append(pso.averageFitness)

    name = "Particles #"
    plotEpochsToConvergence(particlesValues, partEpochsToConv, name)
    plotConvergedPercentage(particlesValues, partEpochsToConv, name)
    plotAveDistance(particlesValues, partAveDistance, name)
    plotAveFitness(particlesValues, partFitness, name)

    # inertia
    inertiaEpochsToConv = []
    inertiaAveDistance = []
    inertiaConverged = []
    inertiaFitness = []
    experiment_folder = problem_folder + "/Inertia"
    createFolder(experiment_folder)

    for val in inertiaValues:
        pso = so.SORun(numberOfEpochs, numberOParticles, val, cognition, social, problem, velocity)
        pso.run()
        inertiaEpochsToConv.append(pso.numOfIterations - 1)
        inertiaAveDistance.append(pso.averageDistance)
        inertiaConverged.append(pso.converged)
        inertiaFitness.append(pso.averageFitness)

    name = "Inertia"
    plotEpochsToConvergence(inertiaValues, inertiaEpochsToConv, name)
    plotConvergedPercentage(inertiaValues, inertiaEpochsToConv, name)
    plotAveDistance(inertiaValues, inertiaAveDistance,name)
    plotAveFitness(inertiaValues, inertiaFitness,name)

    # cognition
    cogEpochsToConv = []
    cogAveDistance = []
    cogConverged = []
    cogFitness = []
    experiment_folder = problem_folder + "/Cognition"
    createFolder(experiment_folder)

    for val in cognitionValues:
        pso = so.SORun(numberOfEpochs, numberOParticles, inertia, val, social, problem, velocity)
        pso.run()
        cogEpochsToConv.append(pso.numOfIterations - 1)
        cogAveDistance.append(pso.averageDistance)
        cogConverged.append(pso.converged)
        cogFitness.append(pso.averageFitness)

    name = "Cognition"
    plotEpochsToConvergence(cognitionValues, cogEpochsToConv, name)
    plotConvergedPercentage(cognitionValues, cogEpochsToConv, name)
    plotAveDistance(cognitionValues, cogAveDistance, name)
    plotAveFitness(cognitionValues, cogFitness, name)

    # social
    socEpochsToConv = []
    socAveDistance = []
    socConverged = []
    socFitness = []
    experiment_folder = problem_folder + "/Social"
    createFolder(experiment_folder)

    for val in socialValues:
        pso = so.SORun(numberOfEpochs, numberOParticles, inertia, cognition, val, problem, velocity)
        pso.run()
        socEpochsToConv.append(pso.numOfIterations - 1)
        socAveDistance.append(pso.averageDistance)
        socConverged.append(pso.converged)
        socFitness.append(pso.averageFitness)

    name = "Social"
    plotEpochsToConvergence(socialValues, socEpochsToConv, name)
    plotConvergedPercentage(socialValues, socEpochsToConv, name)
    plotAveDistance(socialValues, socAveDistance, name)
    plotAveFitness(socialValues, socFitness, name)

    # velocity
    velEpochsToConv = []
    velAveDistance = []
    velConverged = []
    velFitness = []
    experiment_folder = problem_folder + "/Velocity"
    createFolder(experiment_folder)

    for val in velocityValues:
        pso = so.SORun(numberOfEpochs, numberOParticles, inertia, cognition, social, problem, val)
        pso.run()
        velEpochsToConv.append(pso.numOfIterations - 1)
        velAveDistance.append(pso.averageDistance)
        velConverged.append(pso.converged)
        velFitness.append(pso.averageFitness)

    name = "Velocity"
    plotEpochsToConvergence(velocityValues, velEpochsToConv, name)
    plotConvergedPercentage(velocityValues, velEpochsToConv, name)
    plotAveDistance(velocityValues, velAveDistance, name)
    plotAveFitness(velocityValues, velFitness, name)
