

import os
from statistics import mean, stdev
from matplotlib import pyplot as plt

from ej2_utils.radiation_particle import DYNAMIC_FILE_NAME, parse_simulation_output


def run_ej2_1_simulations(dts: list[float], initial_heights: list[float], dynamic_file_name: str):
    energies_list = []

    for dt in dts:

        energies = []

        for initial_height in initial_heights:
            cmd = f"java -DstepSize={dt} -DinitialHeight={initial_height} -DoutputFileName={dynamic_file_name} -jar ./target/molecular-dynamics-ode-simulations-1.0-SNAPSHOT.jar"

            print(f"Executing: {cmd}")
            os.system(cmd)
            print("Done")

            energy = parse_simulation_output()

            energies.append(energy)

        energies_list.append(energies)

    return energies_list


def get_ej2_1_data(energies_list: list[list[dict]], dts: list[float]):
    avg_energies = []
    stdev_energies = []

    for energies in energies_list:

        relative_differences = []

        for energy in energies:
            relative_differences.append(abs(energy["initial"] -
                                            energy["final"]) / energy["initial"])

        avg_energies.append(mean(relative_differences))
        stdev_energies.append(stdev(relative_differences))

    return avg_energies, stdev_energies


def plot_ej2_1(avg_energies: list[float], stdev_energies: list[float], dts: list[float]):
    plt.errorbar(dts, avg_energies, yerr=stdev_energies, fmt='o')
    plt.xlabel("dt")
    plt.ylabel("Relative difference")
    plt.show()


if __name__ == "__main__":
    dts = [1e-10, 1e-9, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2]
    initial_heights = [0, 0.25, 0.5, 0.75, 1]

    energies_list = run_ej2_1_simulations(
        dts, initial_heights, DYNAMIC_FILE_NAME)

    avg_energies, stdev_energies = get_ej2_1_data(energies_list, dts)

    plot_ej2_1(avg_energies, stdev_energies, dts)

    print(f"Average energies: {avg_energies}")
