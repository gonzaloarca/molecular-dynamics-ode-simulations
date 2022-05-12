import enum
import math
import os

import matplotlib.pyplot as plt
import numpy as np


class SimulationStatus(enum.Enum):
    ABSORBED = 'A'
    ESCAPED_LEFT = 'EL'
    ESCAPED_RIGHT = 'ER'
    ESCAPED_BOTTOM = 'EB'
    ESCAPED_TOP = 'ET'


def get_end_state_from_file():
    with open('./summary.txt', 'r') as f:
        for line in f:
            return SimulationStatus(line)


def get_trajectory_from_file():
    input_file = open('dynamic.txt', 'r')
    prev_x, prev_y = 0, 0
    trajectory = 0

    for index, line in enumerate(input_file):
        x, y = map(float, line.split()[0:2])

        if index == 0:
            prev_x, prev_y = x, y
        else:
            trajectory += math.sqrt((x-prev_x)**2 + (y-prev_y)**2)
            prev_x, prev_y = x, y

    input_file.close()

    return trajectory


def get_percentages(end_states):
    absorbed = 0
    escaped_left = 0
    escaped_right = 0
    escaped_bottom = 0
    escaped_top = 0
    for end_state in end_states:
        if end_state == SimulationStatus.ABSORBED:
            absorbed += 1
        elif end_state == SimulationStatus.ESCAPED_LEFT:
            escaped_left += 1
        elif end_state == SimulationStatus.ESCAPED_RIGHT:
            escaped_right += 1
        elif end_state == SimulationStatus.ESCAPED_BOTTOM:
            escaped_bottom += 1
        elif end_state == SimulationStatus.ESCAPED_TOP:
            escaped_top += 1

    return absorbed/len(end_states), escaped_left/len(end_states), escaped_right/len(end_states), escaped_bottom/len(end_states), escaped_top/len(end_states)


def run_with_height_and_velocity(initial_height_ratio, initial_velocity):
    cmd = f"java -DinitialHeightRatio={initial_height_ratio} -DinitialSpeed={initial_velocity} -jar ./target/molecular-dynamics-ode-simulations-1.0-SNAPSHOT.jar"
    print(cmd)
    os.system(cmd)


def plot_end_state_percentages_vs_velocity(absorbed, escaped_left, escaped_right, escaped_bottom, escaped_top, velocities):
    absorbed = np.array(absorbed)
    escaped_left = np.array(escaped_left)
    escaped_right = np.array(escaped_right)
    escaped_bottom = np.array(escaped_bottom)
    escaped_top = np.array(escaped_top)
    velocities = list(map(str, velocities))

    plt.bar(velocities, absorbed)
    plt.bar(velocities, escaped_left,
            bottom=absorbed)
    plt.bar(velocities, escaped_right,
            bottom=absorbed + escaped_left)
    plt.bar(velocities, escaped_bottom,
            bottom=absorbed + escaped_left + escaped_right)
    plt.bar(velocities, escaped_top,
            bottom=absorbed + escaped_left + escaped_right + escaped_bottom)

    plt.xlabel('Velocidad [m/s]', fontsize=15)
    plt.ylabel('Porcentaje de estados finales', fontsize=15)
    plt.ylim(0, 1.5)
    plt.legend(["Absorbidos", "Escapados por la izquierda", "Escapados por la derecha",
               "Escapados por la parte inferior", "Escapados por la parte superior"], fontsize=15)
    plt.show()


def plot_trajectory_vs_velocity(trajectories, velocities, stdev_trajectories):
    plt.errorbar(velocities, trajectories, ls="none",
                 yerr=stdev_trajectories, ecolor='blue', marker='o', color="red", elinewidth=0.5, capsize=5)
    plt.xlabel('Velocidad [m/s]')
    plt.ylabel('Trajectoria promedio [m]')
    plt.show()


def plot_absorbed_trajectories_histogram(absorbed_trajectories_in_velocity):

    for absorbed_trajectories in absorbed_trajectories_in_velocity:
        number_of_bins = int(np.log(len(absorbed_trajectories))/np.log(2))
        plt.hist(absorbed_trajectories, bins=number_of_bins, density=True)
    plt.show()


def main():
    velocities = np.linspace(5e3, 5e4, 7)
    mean_trajectories = []
    stdev_trajectories = []
    initial_height_ratios = np.linspace(0, 1, 50)
    end_states = []
    absorbed = []
    escaped_left = []
    escaped_right = []
    escaped_bottom = []
    escaped_top = []
    absorbed_trajectories_by_velocity = []
    for velocity in velocities:

        trajectories = []
        absorbed_trajectories = []
        for initial_height_ratio in initial_height_ratios:
            run_with_height_and_velocity(initial_height_ratio, velocity)
            trajectory = get_trajectory_from_file()
            trajectories.append(trajectory)
            end_state = get_end_state_from_file()
            end_states.append(end_state)
            if end_state == SimulationStatus.ABSORBED:
                absorbed_trajectories.append(trajectory)

        absorbed_trajectories_by_velocity.append(absorbed_trajectories)

        absorbed_ratio, escaped_left_ratio, escaped_right_ratio, escaped_bottom_ratio, escaped_top_ratio = get_percentages(
            end_states)

        absorbed.append(absorbed_ratio)
        escaped_left.append(escaped_left_ratio)
        escaped_right.append(escaped_right_ratio)
        escaped_bottom.append(escaped_bottom_ratio)
        escaped_top.append(escaped_top_ratio)

        stdev_trajectories.append(np.std(trajectories))
        mean_trajectories.append(np.mean(trajectories))

    plot_absorbed_trajectories_histogram(
        absorbed_trajectories_by_velocity)
    plot_end_state_percentages_vs_velocity(
        absorbed, escaped_left, escaped_right, escaped_bottom, escaped_top, velocities)
    plot_trajectory_vs_velocity(
        mean_trajectories, velocities, stdev_trajectories)


if __name__ == '__main__':
    main()
