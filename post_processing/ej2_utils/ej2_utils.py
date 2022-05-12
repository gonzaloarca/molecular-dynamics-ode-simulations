from matplotlib import pyplot as plt


MATTER_FILE_NAME = "matter.txt"
DYNAMIC_FILE_NAME = "dynamic.txt"
STATIC_FILE_NAME = "static.txt"

PARTICLE_XYZ_MOVEMENT_FILE_NAME = "particle_movement.xyz"
MATTER_XYZ_FILE_NAME = "matter.xyz"


def parse_static_parameters():
    static_parameters = {}

    with open(STATIC_FILE_NAME, 'r') as f:
        for line_number, line in enumerate(f):
            if line_number == 0:
                static_parameters["distance_between_particles"] = float(line.split()[
                                                                        0])
            elif line_number == 1:
                static_parameters["particles_per_row"] = int(line.split()[0])
            elif line_number == 2:
                static_parameters["box_height"] = float(line.split()[0])
                static_parameters["box_width"] = float(line.split()[1])
            elif line_number == 3:
                static_parameters["initial_height"] = float(line.split()[0])
            elif line_number == 4:
                static_parameters["initial_speed"] = float(line.split()[0])
            elif line_number == 5:
                static_parameters["mass"] = float(line.split()[0])
            elif line_number == 6:
                static_parameters["charge"] = float(line.split()[0])
            elif line_number == 7:
                static_parameters["step_size"] = float(line.split()[0])
            elif line_number == 8:
                static_parameters["save_frequency"] = float(line.split()[0])

    return static_parameters


def parse_simulation_output():

    static_parameters = parse_static_parameters()

    mass = static_parameters["mass"]

    particle_movement_output_file = open(PARTICLE_XYZ_MOVEMENT_FILE_NAME, 'w')

    energy = {
        "initial": None,
        "final": None
    }

    with open(DYNAMIC_FILE_NAME) as f:
        for line_number, line in enumerate(f):
            # x y vx vy potential_energy
            line = line.split()

            particle_movement_output_file.write(
                f"1\nCOMMENT\n{' '.join(line)} -1\n")

            total_energy = calculate_total_energy(mass, float(
                line[2]), float(line[3]), float(line[4]))

            if line_number == 0:
                energy["initial"] = total_energy
            else:
                energy["final"] = total_energy

    if energy["final"] is None:
        energy["final"] = energy["initial"]

    particle_movement_output_file.close()

    # plt.plot(energy_list)

    return energy


def generate_matter_file():

    static_data_file = open(STATIC_FILE_NAME, 'r')

    number_of_particles = int(static_data_file.readlines()[1]) ** 2

    matter_output_file = open(MATTER_XYZ_FILE_NAME, 'w')
    matter_output_file.write(f"{number_of_particles}\nCOMMENT\n")

    with open(MATTER_FILE_NAME, 'r') as f:

        for line in f:
            matter_output_file.write(line)

    matter_output_file.close()


def calculate_total_energy(mass, vx, vy, potential_energy):
    return mass * (vx ** 2 + vy ** 2) * 0.5 + potential_energy


def main():
    generate_matter_file()
    energy = parse_simulation_output()


if __name__ == "__main__":
    main()
