/*
 * Copyright (c) 2025 Akbet Ereke
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.focusify.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun localDateToString(localDate: LocalDate?): String? {
        return localDate?.toString()
    }

    @TypeConverter
    fun stringToLocalDate(date: String?): LocalDate? {
        return if (date != null) LocalDate.parse(date) else null
    }
}