from curses import flash
import enum
import math
import os
from pprint import pprint

import matplotlib.pyplot as plt
import numpy as np


class SimulationStatus(enum.Enum):
    ABSORBED = 'A'
    ESCAPED_LEFT = 'EL'
    ESCAPED_RIGHT = 'ER'
    ESCAPED_BOTTOM = 'EB'
    ESCAPED_TOP = 'ET'


def percentage_notation(x, pos):
    if x > 1:
        return ''
    return '{:.2%}'.format(x)


def sci_notation(number, sig_fig=2):
    ret_string = "{0:.2e}".format(number, sig_fig)
    a, b = ret_string.split("e")
    # remove leading "+" and strip leading zeros
    b = int(b)
    return a + f"$\\times 10^{{{b}}}$"


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
    velocities = list(map(str, map(int, velocities)))

    fig, ax = plt.subplots()

    ax.yaxis.set_major_formatter(
        plt.FuncFormatter(percentage_notation))

    legends = ["Absorbidos", "Escapados por la izquierda", "Escapados por la derecha",
               "Escapados por la parte inferior", "Escapados por la parte superior"]

    ind = np.arange(len(velocities))
    width = 0.17
    ax.bar(ind-2*width, absorbed, width=width, align='center')
    ax.bar(ind-width, escaped_left, width=width, align='center')
    ax.bar(ind, escaped_right, width=width, align='center')
    ax.bar(ind+width, escaped_bottom, width=width, align='center')
    ax.bar(ind+2*width, escaped_top, width=width, align='center')
    ax.set_xticks(ind)
    ax.set_xticklabels(velocities)
    ax.autoscale(tight=True)
    plt.xlabel('Velocidad [m/s]', fontsize=15)
    plt.ylabel('Porcentaje de estados finales', fontsize=15)
    plt.ylim(0, 1.5)
    plt.legend(legends, fontsize=15)
    plt.show()


def plot_trajectory_vs_velocity(trajectories, velocities, stdev_trajectories):
    plt.errorbar(velocities, trajectories, ls="none",
                 yerr=stdev_trajectories, ecolor='blue', marker='o', color="red", elinewidth=0.5, capsize=5)
    plt.xlabel('Velocidad [m/s]')
    plt.ylabel('Trajectoria promedio [m]')
    plt.show()


def plot_absorbed_trajectories_histogram(absorbed_trajectories_in_velocity, velocities):

    number_of_bins = 5000

    for absorbed_trajectory in absorbed_trajectories_in_velocity:
        bin_width = max(absorbed_trajectory) / number_of_bins
        bins = np.empty(number_of_bins)
        bins.fill(0)
        for trajectory in absorbed_trajectory:
            bin_index = int(trajectory / bin_width)
            bin_index = min(bin_index, number_of_bins-1)
            bins[bin_index] += 1

        density_bins = bins / \
            (len(absorbed_trajectories_in_velocity) * bin_width)

        x = np.linspace(0, max(absorbed_trajectory), number_of_bins)

        plt.plot(x, density_bins)

        fig, ax = plt.subplots()

        ax.xaxis.set_major_formatter(
            plt.FuncFormatter(sci_notation))
        ax.yaxis.set_major_formatter(
            plt.FuncFormatter(sci_notation))

    plt.xlabel('Trajectoria Absorbidos [m]')
    plt.ylabel('Densidad de probabilidad de absorcion')
    plt.legend([f"$V_0$ = {int(velocity)} [m/s]" for velocity in velocities])
    plt.show()


def save_file_data(velocity, initial_height, trajectory, finish_reason: SimulationStatus):
    with open('./output_files/ej2_2.txt', 'a') as f:
        f.write(f"{velocity} {initial_height} {trajectory} {finish_reason.value}\n")


def read_file_data(number_of_heights_per_velocity):
    mean_trajectories = []
    stdev_trajectories = []
    absorbed = []
    escaped_left = []
    escaped_right = []
    escaped_bottom = []
    escaped_top = []
    end_states = []
    top_trajectories_per_velocity = [
        {'velocity': 0, 'trajectory': 0, 'initialHeightRatio': 0}
    ]
    absorbed_trajectories_by_velocity = []
    velocity_index = 0
    with open('./output_files/ej2_2.txt', 'r') as f:

        absorbed_trajectories = []
        trajectories = []

        for i, line in enumerate(f):

            if i != 0 and i % number_of_heights_per_velocity == 0:

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

                absorbed_trajectories = []
                trajectories = []
                end_states = []
                top_trajectories_per_velocity.append(
                    {'velocity': 0, 'trajectory': 0, 'initialHeightRatio': 0})
                velocity_index += 1

            else:

                trajectory = float(line.split()[2])
                trajectories.append(trajectory)
                end_state = SimulationStatus(line.split()[3])
                end_states.append(end_state)

                if trajectory > top_trajectories_per_velocity[velocity_index]['trajectory']:
                    top_trajectories_per_velocity[velocity_index]['trajectory'] = trajectory
                    top_trajectories_per_velocity[velocity_index]['initialHeightRatio'] = float(
                        line.split()[1])
                    top_trajectories_per_velocity[velocity_index]['velocity'] = float(line.split()[
                                                                                      0])

                if end_state == SimulationStatus.ABSORBED:

                    absorbed_trajectories.append(trajectory)

        # 2.2
        stdev_trajectories.append(np.std(trajectories))
        mean_trajectories.append(np.mean(trajectories))

        # 2.3
        absorbed_ratio, escaped_left_ratio, escaped_right_ratio, escaped_bottom_ratio, escaped_top_ratio = get_percentages(
            end_states)
        absorbed.append(absorbed_ratio)
        escaped_left.append(escaped_left_ratio)
        escaped_right.append(escaped_right_ratio)
        escaped_bottom.append(escaped_bottom_ratio)
        escaped_top.append(escaped_top_ratio)

        # 2.4
        absorbed_trajectories_by_velocity.append(absorbed_trajectories)

    return mean_trajectories, stdev_trajectories, absorbed, escaped_left, escaped_right, escaped_bottom, escaped_top, absorbed_trajectories_by_velocity, top_trajectories_per_velocity


def generate_data(velocities, initial_heights_ratios):
    for velocity in velocities:
        for initial_height_ratio in initial_heights_ratios:
            run_with_height_and_velocity(initial_height_ratio, velocity)
            trajectory = get_trajectory_from_file()
            end_state = get_end_state_from_file()
            save_file_data(velocity, initial_height_ratio,
                           trajectory, end_state)


def main():
    velocities = np.linspace(5e3, 5e4, 7)
    mean_trajectories = []
    stdev_trajectories = []
    number_of_heights_per_velocity = 200
    initial_height_ratios = np.linspace(0, 1, number_of_heights_per_velocity)

    # generate_data(velocities, initial_height_ratios)

    mean_trajectories, stdev_trajectories, absorbed, escaped_left, escaped_right, escaped_bottom, escaped_top, absorbed_trajectories_by_velocity, top_trajectories_per_velocity = read_file_data(
        number_of_heights_per_velocity)

    pprint(top_trajectories_per_velocity)
    # 2.2
    # plot_trajectory_vs_velocity(
    #     mean_trajectories, velocities, stdev_trajectories)

    # 2.3
    plot_end_state_percentages_vs_velocity(
        absorbed, escaped_left, escaped_right, escaped_bottom, escaped_top, velocities)

    # 2.4
    # plot_absorbed_trajectories_histogram(
    #     absorbed_trajectories_by_velocity, velocities)


if __name__ == '__main__':
    main()
